package fr.raksrinana.channelpointsminer.runnable;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.inventory.InventoryData;
import fr.raksrinana.channelpointsminer.api.gql.data.types.Inventory;
import fr.raksrinana.channelpointsminer.api.gql.data.types.TimeBasedDrop;
import fr.raksrinana.channelpointsminer.api.gql.data.types.TimeBasedDropSelfEdge;
import fr.raksrinana.channelpointsminer.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
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
				.map(TimeBasedDrop::getSelf)
				.filter(Objects::nonNull)
				.filter(timeBasedDropSelfEdge -> !timeBasedDropSelfEdge.isClaimed())
				.map(TimeBasedDropSelfEdge::getDropInstanceId)
				.filter(Objects::nonNull)
				.toList();
		
		if(dropsToClaim.isEmpty()){
			return;
		}
		
		log.info("Claiming drops {}", dropsToClaim);
		dropsToClaim.forEach(miner.getGqlApi()::dropsPageClaimDropRewards);
	}
}
