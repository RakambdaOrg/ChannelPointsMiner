package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.Main;
import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import fr.raksrinana.twitchminer.miner.runnables.SendMinutesWatched;
import fr.raksrinana.twitchminer.miner.runnables.UpdateChannelPointsContext;
import fr.raksrinana.twitchminer.miner.runnables.UpdateStreamInfo;
import fr.raksrinana.twitchminer.miner.runnables.WebSocketPing;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName.*;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class Miner implements AutoCloseable, IMiner{
	@Getter
	private final Set<Streamer> streamers;
	@Getter
	private final TwitchWebSocketPool webSocketPool;
	private final ScheduledExecutorService scheduledExecutor;
	
	private final UpdateChannelPointsContext updateChannelPointsContext;
	private final UpdateStreamInfo updateStreamInfo;
	
	public Miner(){
		streamers = new HashSet<>();
		webSocketPool = new TwitchWebSocketPool();
		scheduledExecutor = Executors.newScheduledThreadPool(4);
		
		updateChannelPointsContext = new UpdateChannelPointsContext(this);
		updateStreamInfo = new UpdateStreamInfo(this);
	}
	
	public void addStreamer(@NotNull Streamer streamer){
		if(streamers.contains(streamer)){
			log.debug("Streamer {} is already being mined", streamer);
			return;
		}
		log.info("Added to the mining list: {}", streamer);
		
		updateStreamInfo.update(streamer);
		updateChannelPointsContext.update(streamer);
		
		webSocketPool.listenTopic(VIDEO_PLAYBACK_BY_ID, streamer.getId());
		
		if(streamer.getSettings().isMakePredictions()){
			webSocketPool.listenTopic(PREDICTIONS_USER_V1, Main.getTwitchLogin().getUserId());
			webSocketPool.listenTopic(PREDICTIONS_CHANNEL_V1, streamer.getId());
		}
		if(streamer.getSettings().isFollowRaid()){
			webSocketPool.listenTopic(RAID, streamer.getId());
		}
		streamers.add(streamer);
	}
	
	public void start(){
		log.info("Starting miner");
		
		scheduledExecutor.scheduleWithFixedDelay(updateChannelPointsContext, 0, 30, MINUTES);
		scheduledExecutor.scheduleWithFixedDelay(updateStreamInfo, 0, 10, MINUTES);
		scheduledExecutor.scheduleWithFixedDelay(new SendMinutesWatched(this), 0, 1, MINUTES);
		scheduledExecutor.scheduleAtFixedRate(new WebSocketPing(this), 25, 25, SECONDS);
		
		webSocketPool.listenTopic(COMMUNITY_POINTS_USER_V1, Main.getTwitchLogin().getUserId());
	}
	
	public boolean hasStreamerWithUsername(@NotNull String username){
		return streamers.stream().anyMatch(s -> Objects.equals(s.getUsername(), username));
	}
	
	@Override
	public void close(){
		scheduledExecutor.shutdown();
		webSocketPool.close();
	}
}
