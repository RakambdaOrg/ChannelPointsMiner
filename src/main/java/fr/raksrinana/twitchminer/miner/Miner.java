package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.Main;
import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.types.Game;
import fr.raksrinana.twitchminer.api.twitch.MinuteWatchedProperties;
import fr.raksrinana.twitchminer.api.twitch.MinuteWatchedRequest;
import fr.raksrinana.twitchminer.api.twitch.TwitchApi;
import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketPool;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import static fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName.*;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class Miner implements AutoCloseable{
	private static final String SITE_PLAYER = "site";
	
	private final Set<Streamer> streamers;
	private final ScheduledExecutorService scheduledExecutor;
	private final TwitchWebSocketPool websocketPool;
	
	public Miner(){
		streamers = new HashSet<>();
		scheduledExecutor = Executors.newScheduledThreadPool(4);
		websocketPool = new TwitchWebSocketPool();
	}
	
	public void addStreamer(@NotNull Streamer streamer){
		log.info("Added to the mining list: {}", streamer);
		
		updateStreamInfo(streamer);
		updateChannelPointsContext(streamer);
		
		websocketPool.listenTopic(VIDEO_PLAYBACK_BY_ID, streamer.getId());
		
		if(streamer.getSettings().isMakePredictions()){
			websocketPool.listenTopic(PREDICTIONS_USER_V1, Main.getTwitchLogin().getUserId());
			websocketPool.listenTopic(PREDICTIONS_CHANNEL_V1, streamer.getId());
		}
		if(streamer.getSettings().isFollowRaid()){
			websocketPool.listenTopic(RAID, streamer.getId());
		}
		streamers.add(streamer);
	}
	
	public void start(){
		log.info("Starting miner");
		
		scheduledExecutor.scheduleWithFixedDelay(this::updateChannelPointsContext, 0, 30, MINUTES);
		scheduledExecutor.scheduleWithFixedDelay(this::updateStreamInfo, 0, 10, MINUTES);
		scheduledExecutor.scheduleWithFixedDelay(this::sendMinutesWatched, 0, 1, MINUTES);
		scheduledExecutor.scheduleWithFixedDelay(this::websocketPing, 30, 30, SECONDS);
		
		websocketPool.listenTopic(COMMUNITY_POINTS_USER_V1, Main.getTwitchLogin().getUserId());
	}
	
	private void websocketPing(){
		websocketPool.ping();
	}
	
	@SneakyThrows
	private void sendMinutesWatched(){
		log.debug("Sending minutes watched");
		var toSendMinutesWatched = streamers.stream()
				.filter(Streamer::isStreaming)
				.filter(streamer -> Objects.nonNull(streamer.getSpadeUrl()))
				.limit(2)
				.collect(Collectors.toSet());
		
		int streamerScheduled = 0;
		for(var streamer : toSendMinutesWatched){
			log.debug("Sending minutes watched for {}", streamer);
			var request = new MinuteWatchedRequest(MinuteWatchedProperties.builder()
					.channelId(streamer.getId())
					.broadcastId(streamer.getBroadcastId().orElse(null))
					.player(SITE_PLAYER)
					.userId(Main.getTwitchLogin().getUserId())
					.game(streamer.getGame().map(Game::getName).orElse(null))
					.build());
			
			TwitchApi.sendMinutesWatched(streamer.getSpadeUrl(), request);
			
			randomSleep(100, 50);
		}
		
		log.debug("Done sending minutes watched");
	}
	
	@SneakyThrows
	private void updateStreamInfo(){
		log.debug("Updating stream info");
		for(var streamer : streamers){
			updateStreamInfo(streamer);
			randomSleep(500, 100);
		}
		log.debug("Done updating stream info");
	}
	
	@SneakyThrows
	private void updateStreamInfo(@NotNull Streamer streamer){
		log.trace("Updating stream info for {}", streamer);
		var wasStreaming = streamer.isStreaming();
		
		GQLApi.videoPlayerStreamInfoOverlayChannel(streamer.getUsername())
				.map(GQLResponse::getData)
				.ifPresentOrElse(
						streamer::setVideoPlayerStreamInfoOverlayChannel,
						() -> streamer.setVideoPlayerStreamInfoOverlayChannel(null));
		
		if(streamer.isStreaming() && !wasStreaming){
			Optional.ofNullable(streamer.getUrl())
					.flatMap(TwitchApi::getSpadeUrl)
					.ifPresent(streamer::setSpadeUrl);
		}
		
		if(streamer.updateCampaigns() && streamer.isStreaming() && streamer.isStreamingGame()){
			GQLApi.dropsHighlightServiceAvailableDrops(streamer.getId())
					.map(GQLResponse::getData)
					.ifPresentOrElse(
							streamer::setDropsHighlightServiceAvailableDrops,
							() -> streamer.setDropsHighlightServiceAvailableDrops(null));
		}
		else{
			streamer.setDropsHighlightServiceAvailableDrops(null);
		}
	}
	
	@SneakyThrows
	private void updateChannelPointsContext(){
		log.debug("Updating channel points context");
		for(var streamer : streamers){
			updateChannelPointsContext(streamer);
			randomSleep(500, 100);
		}
		log.debug("Done updating channel points context");
	}
	
	@SneakyThrows
	private void updateChannelPointsContext(@NotNull Streamer streamer){
		log.trace("Updating channel points context for {}", streamer);
		
		GQLApi.channelPointsContext(streamer.getUsername())
				.map(GQLResponse::getData)
				.ifPresentOrElse(
						streamer::setChannelPointsContext,
						() -> streamer.setChannelPointsContext(null));
	}
	
	private void randomSleep(long delay, long delta) throws InterruptedException{
		long actualDelay = delay - delta / 2 + ThreadLocalRandom.current().nextLong(delta);
		if(actualDelay > 0){
			Thread.sleep(actualDelay);
		}
	}
	
	@Override
	public void close(){
		scheduledExecutor.shutdown();
		websocketPool.close();
	}
}
