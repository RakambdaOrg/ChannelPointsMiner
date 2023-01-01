package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.inventory.InventoryData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Inventory;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDrop;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDropSelfEdge;
import fr.rakambda.channelpointsminer.miner.event.impl.DropClaimEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.DropClaimedEvent;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Log4j2
@RequiredArgsConstructor
public class SyncInventory implements Runnable{
	@NotNull
	private final IMiner miner;
	
	@Override
	public void run(){
		try(var ignored = LogContext.with(miner)){
			if(!needUpdate()){
				log.trace("Skipped inventory syncing");
				return;
			}
			log.debug("Syncing inventory");
			try{
				var inventory = miner.getGqlApi().inventory()
						.map(GQLResponse::getData)
						.orElse(null);
				miner.getMinerData().setInventory(inventory);
				if(Objects.nonNull(inventory)){
					claimDrops(inventory);
				}
				log.debug("Done syncing inventory");
			}
			catch(Exception e){
				log.error("Failed to sync inventory", e);
			}
		}
	}
	
	private boolean needUpdate(){
		return miner.getStreamers().stream().anyMatch(Streamer::isParticipateCampaigns);
	}
	
	private void claimDrops(@NotNull InventoryData inventory){
		var dropsToClaim = ofNullable(inventory.getCurrentUser().getInventory())
				.map(Inventory::getDropCampaignsInProgress).stream().flatMap(Collection::stream)
				.flatMap(dropCampaign -> dropCampaign.getTimeBasedDrops().stream())
				.filter(timeBasedDrop -> Objects.nonNull(timeBasedDrop.getSelf()))
				.filter(timeBasedDrop -> !timeBasedDrop.getSelf().isClaimed())
				.filter(timeBasedDrop -> Objects.nonNull(timeBasedDrop.getSelf().getDropInstanceId()))
				.toList();
		
		if(dropsToClaim.isEmpty()){
			return;
		}
		
		log.debug("Claiming drops {}", dropsToClaim);
		dropsToClaim.forEach(this::claimDrop);
	}
	
	private void claimDrop(@NotNull TimeBasedDrop timeBasedDrop){
		miner.onEvent(new DropClaimEvent(miner, timeBasedDrop, TimeFactory.now()));
		
		var dropInstanceId = Optional.ofNullable(timeBasedDrop.getSelf()).map(TimeBasedDropSelfEdge::getDropInstanceId);
		if(dropInstanceId.isEmpty()){
			log.error("Failed to claim drop, value is null");
			return;
		}
		
		miner.getGqlApi().dropsPageClaimDropRewards(dropInstanceId.get())
				.filter(r -> {
					if(!r.isError()){
						return true;
					}
					log.error("Failed to claim drop due to `{}` | {}", r.getError(), r.getErrors());
					return false;
				})
				.map(r -> new DropClaimedEvent(miner, timeBasedDrop, TimeFactory.now()))
				.ifPresent(miner::onEvent);
	}
}
