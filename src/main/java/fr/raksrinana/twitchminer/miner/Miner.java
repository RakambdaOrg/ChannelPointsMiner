package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.response.GQLResponse;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.MINUTES;

@Log4j2
public class Miner{
	private final Set<Streamer> streamers;
	private final ScheduledExecutorService scheduledExecutor;
	
	public Miner(){
		streamers = new HashSet<>();
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	}
	
	public void addStreamer(@NotNull Streamer streamer){
		log.info("Added to the mining list: {}", streamer.getUsername());
		streamers.add(streamer);
	}
	
	public void mine(){
		log.info("Starting miner");
		
		scheduledExecutor.scheduleWithFixedDelay(this::updateChannelPointsContext, 0, 30, MINUTES);
	}
	
	@SneakyThrows
	private void updateChannelPointsContext(){
		log.debug("Updating channel points context");
		for(var streamer : streamers){
			log.trace("Updating channel points context for {}", streamer.getUsername());
			GQLApi.channelPointsContext(streamer.getUsername())
					.map(GQLResponse::getData)
					.ifPresent(streamer::setChannelPointsContext);
			Thread.sleep(500);
		}
		log.debug("Done updating channel points context");
	}
}
