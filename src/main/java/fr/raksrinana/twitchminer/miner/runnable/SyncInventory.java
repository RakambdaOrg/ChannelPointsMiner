package fr.raksrinana.twitchminer.miner.runnable;

import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.miner.IMiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
@RequiredArgsConstructor
public class SyncInventory implements Runnable{
	@NotNull
	private final IMiner miner;
	
	@Override
	public void run(){
		log.debug("Syncing inventory");
		try{
			var inventory = miner.getGqlApi().inventory()
					.map(GQLResponse::getData)
					.orElse(null);
			miner.getMinerData().setInventory(inventory);
			log.debug("Done syncing inventory");
		}
		catch(Exception e){
			log.error("Failed to sync inventory", e);
		}
	}
}
