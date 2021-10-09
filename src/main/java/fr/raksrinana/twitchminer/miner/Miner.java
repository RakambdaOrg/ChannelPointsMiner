package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.twitchminer.api.helix.HelixApi;
import fr.raksrinana.twitchminer.api.kraken.KrakenApi;
import fr.raksrinana.twitchminer.api.passport.PassportApi;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import fr.raksrinana.twitchminer.api.passport.exceptions.CaptchaSolveRequired;
import fr.raksrinana.twitchminer.api.twitch.TwitchApi;
import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.twitchminer.config.Configuration;
import fr.raksrinana.twitchminer.factory.ApiFactory;
import fr.raksrinana.twitchminer.factory.MinerRunnableFactory;
import fr.raksrinana.twitchminer.factory.StreamerSettingsFactory;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import fr.raksrinana.twitchminer.miner.runnables.UpdateChannelPointsContext;
import fr.raksrinana.twitchminer.miner.runnables.UpdateStreamInfo;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import static fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName.*;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class Miner implements AutoCloseable, IMiner{
	private final Configuration configuration;
	private final PassportApi passportApi;
	
	@Getter
	private final Set<Streamer> streamers;
	@Getter
	private final TwitchWebSocketPool webSocketPool;
	private final ScheduledExecutorService scheduledExecutor;
	private final StreamerSettingsFactory streamerSettingsFactory;
	
	private UpdateChannelPointsContext updateChannelPointsContext;
	private UpdateStreamInfo updateStreamInfo;
	
	@Getter
	private TwitchLogin twitchLogin;
	@Getter
	private GQLApi gqlApi;
	private KrakenApi krakenApi;
	private HelixApi helixApi;
	@Getter
	private TwitchApi twitchApi;
	
	public Miner(@NotNull Configuration configuration,
			@NotNull PassportApi passportApi,
			@NotNull StreamerSettingsFactory streamerSettingsFactory,
			@NotNull TwitchWebSocketPool webSocketPool,
			@NotNull ScheduledExecutorService scheduledExecutor){
		this.configuration = configuration;
		this.passportApi = passportApi;
		this.streamerSettingsFactory = streamerSettingsFactory;
		this.webSocketPool = webSocketPool;
		this.scheduledExecutor = scheduledExecutor;
		
		streamers = new HashSet<>();
	}
	
	/**
	 * Initialize everything and starts the miner.
	 *
	 * @throws IllegalStateException If the login failed.
	 */
	public void start(){
		log.info("Starting miner");
		
		login();
		loadStreamersFromConfiguration();
		loadStreamersFromFollows();
		
		scheduledExecutor.scheduleWithFixedDelay(getUpdateChannelPointsContext(), 0, 30, MINUTES);
		scheduledExecutor.scheduleWithFixedDelay(getUpdateStreamInfo(), 0, 10, MINUTES);
		scheduledExecutor.scheduleWithFixedDelay(MinerRunnableFactory.getSendMinutesWatched(this), 0, 1, MINUTES);
		scheduledExecutor.scheduleAtFixedRate(MinerRunnableFactory.getWebSocketPing(this), 25, 25, SECONDS);
		
		listenTopic(COMMUNITY_POINTS_USER_V1, getTwitchLogin().getUserId());
	}
	
	private void loadStreamersFromConfiguration(){
		log.info("Loading streamers from configuration");
		configuration.getStreamers().stream()
				.map(streamer -> {
					var user = gqlApi.reportMenuItem(streamer.getUsername())
							.map(GQLResponse::getData)
							.map(ReportMenuItemData::getUser)
							.orElse(null);
					if(Objects.isNull(user)){
						log.error("Failed to get streamer " + streamer.getUsername());
						return null;
					}
					return new Streamer(user.getId(), streamer.getUsername(), streamerSettingsFactory.readStreamerSettings());
				})
				.filter(Objects::nonNull)
				.forEach(this::addStreamer);
	}
	
	private void loadStreamersFromFollows(){
		if(configuration.isLoadFollows()){
			log.info("Loading streamers from follow list");
			krakenApi.getFollows().stream()
					.filter(follow -> !hasStreamerWithUsername(follow.getChannel().getName()))
					.map(follow -> new Streamer(follow.getChannel().getId(), follow.getChannel().getName(), streamerSettingsFactory.readStreamerSettings()))
					.forEach(this::addStreamer);
		}
	}
	
	/**
	 * Login to twitch.
	 *
	 * @throws IllegalStateException If the login failed.
	 */
	private void login(){
		try{
			twitchLogin = passportApi.login();
			gqlApi = ApiFactory.getGqlApi(twitchLogin);
			helixApi = ApiFactory.getHelixApi(twitchLogin);
			krakenApi = ApiFactory.getKrakenApi(twitchLogin);
			twitchApi = ApiFactory.getTwitchApi();
		}
		catch(CaptchaSolveRequired e){
			throw new IllegalStateException("A captcha solve is required, please log in through your browser and solve it");
		}
		catch(Exception e){
			throw new IllegalStateException("Failed to login", e);
		}
	}
	
	@Override
	public void addStreamer(@NotNull Streamer streamer){
		if(streamers.contains(streamer)){
			log.debug("Streamer {} is already being mined", streamer);
			return;
		}
		log.info("Added to the mining list: {}", streamer);
		
		getUpdateStreamInfo().update(streamer);
		getUpdateChannelPointsContext().update(streamer);
		
		listenTopic(VIDEO_PLAYBACK_BY_ID, streamer.getId());
		
		if(streamer.getSettings().isMakePredictions()){
			listenTopic(PREDICTIONS_USER_V1, getTwitchLogin().getUserId());
			listenTopic(PREDICTIONS_CHANNEL_V1, streamer.getId());
		}
		if(streamer.getSettings().isFollowRaid()){
			listenTopic(RAID, streamer.getId());
		}
		streamers.add(streamer);
	}
	
	@Override
	public boolean hasStreamerWithUsername(@NotNull String username){
		return streamers.stream().anyMatch(s -> username.equalsIgnoreCase(s.getUsername()));
	}
	
	private void listenTopic(@NotNull TopicName name, @NotNull String target){
		webSocketPool.listenTopic(Topics.buildFromName(name, target, twitchLogin.getAccessToken()));
	}
	
	private UpdateChannelPointsContext getUpdateChannelPointsContext(){
		if(Objects.isNull(updateChannelPointsContext)){
			updateChannelPointsContext = MinerRunnableFactory.getUpdateChannelPointsContext(this);
		}
		return updateChannelPointsContext;
	}
	
	private UpdateStreamInfo getUpdateStreamInfo(){
		if(Objects.isNull(updateChannelPointsContext)){
			updateStreamInfo = MinerRunnableFactory.getUpdateStreamInfo(this);
		}
		return updateStreamInfo;
	}
	
	@Override
	public void close(){
		scheduledExecutor.shutdown();
		webSocketPool.close();
	}
}
