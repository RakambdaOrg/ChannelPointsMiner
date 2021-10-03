package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.Main;
import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.types.Game;
import fr.raksrinana.twitchminer.api.twitch.MinuteWatchedProperties;
import fr.raksrinana.twitchminer.api.twitch.MinuteWatchedRequest;
import fr.raksrinana.twitchminer.api.twitch.TwitchApi;
import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketClient;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class Miner{
	private static final String SITE_PLAYER = "site";
	
	private final Set<Streamer> streamers;
	private final ScheduledExecutorService scheduledExecutor;
	private TwitchWebSocketClient websocket;
	
	public Miner(){
		streamers = new HashSet<>();
		scheduledExecutor = Executors.newScheduledThreadPool(4);
	}
	
	public void addStreamer(@NotNull Streamer streamer){
		log.info("Added to the mining list: {}", streamer.getUsername());
		streamers.add(streamer);
	}
	
	public void start() throws InterruptedException{
		log.info("Starting miner");
		
		websocket = new TwitchWebSocketClient();
		websocket.connectBlocking();
		
		// scheduledExecutor.scheduleWithFixedDelay(this::updateChannelPointsContext, 0, 30, MINUTES);
		// scheduledExecutor.scheduleWithFixedDelay(this::updateStreamInfo, 0, 10, MINUTES);
		// scheduledExecutor.scheduleWithFixedDelay(this::sendMinutesWatched, 0, 1, MINUTES);
		scheduledExecutor.scheduleWithFixedDelay(this::websocketPing, 0, 30, SECONDS);
		
		websocket.listenTopic(TopicName.COMMUNITY_POINTS_USER_V1, Main.getTwitchLogin().getUserId());
	}
	
	private void websocketPing(){
		if(websocket.isOpen() && !websocket.isClosing()){
			websocket.ping();
		}
	}
	
	@SneakyThrows
	private void sendMinutesWatched(){
		log.debug("Sending minutes watched");
		var toSendMinutesWatched = streamers.stream()
				.filter(Streamer::isStreaming)
				.filter(streamer -> Objects.nonNull(streamer.getSpadeUrl()))
				.limit(2)
				.collect(Collectors.toSet());
		
		for(var streamer : toSendMinutesWatched){
			log.debug("Sending minutes watched for {}", streamer.getUsername());
			var request = new MinuteWatchedRequest(MinuteWatchedProperties.builder()
					.channelId(streamer.getId())
					.broadcastId(streamer.getBroadcastId().orElse(null))
					.player(SITE_PLAYER)
					.userId(Main.getTwitchLogin().getUserId())
					.game(streamer.getGame().map(Game::getName).orElse(null))
					.build());
			
			if(TwitchApi.sendMinutesWatched(streamer.getSpadeUrl(), request)){
			
			}
		}
		
		log.debug("Done sending minutes watched");
	}
	
	@SneakyThrows
	private void updateStreamInfo(){
		log.debug("Updating stream info");
		for(var streamer : streamers){
			log.trace("Updating stream info for {}", streamer.getUsername());
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
			
			Thread.sleep(500);
		}
		log.debug("Done updating stream info");
	}
	
	@SneakyThrows
	private void updateChannelPointsContext(){
		log.debug("Updating channel points context");
		for(var streamer : streamers){
			log.trace("Updating channel points context for {}", streamer.getUsername());
			
			GQLApi.channelPointsContext(streamer.getUsername())
					.map(GQLResponse::getData)
					.ifPresentOrElse(
							streamer::setChannelPointsContext,
							() -> streamer.setChannelPointsContext(null));
			
			Thread.sleep(500);
		}
		log.debug("Done updating channel points context");
	}
}
