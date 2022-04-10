package fr.raksrinana.channelpointsminer.miner.runnable;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.inventory.InventoryData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.Inventory;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.TimeBasedDrop;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.TimeBasedDropSelfEdge;
import fr.raksrinana.channelpointsminer.miner.event.impl.DropClaimEvent;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Objects;
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
		dropsToClaim.stream()
				.map(drop -> new DropClaimEvent(miner, drop, TimeFactory.now()))
				.forEach(miner::onEvent);
		dropsToClaim.stream()
				.map(TimeBasedDrop::getSelf)
				.map(TimeBasedDropSelfEdge::getDropInstanceId)
				.forEach(miner.getGqlApi()::dropsPageClaimDropRewards);
	}
}
