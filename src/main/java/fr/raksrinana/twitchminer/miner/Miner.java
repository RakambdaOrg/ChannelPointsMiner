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
import fr.raksrinana.twitchminer.api.ws.TwitchMessageListener;
import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.twitchminer.api.ws.data.message.ClaimAvailable;
import fr.raksrinana.twitchminer.api.ws.data.message.Message;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.twitchminer.config.Configuration;
import fr.raksrinana.twitchminer.factory.*;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import fr.raksrinana.twitchminer.miner.handler.MessageHandler;
import fr.raksrinana.twitchminer.miner.runnables.UpdateChannelPointsContext;
import fr.raksrinana.twitchminer.miner.runnables.UpdateStreamInfo;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import static fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName.*;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class Miner implements AutoCloseable, IMiner, TwitchMessageListener{
	private final Configuration configuration;
	private final PassportApi passportApi;
	
	@Getter
	private final Set<Streamer> streamers;
	@Getter
	private final TwitchWebSocketPool webSocketPool;
	private final ScheduledExecutorService scheduledExecutor;
	private final ExecutorService handlerExecutor;
	private final StreamerSettingsFactory streamerSettingsFactory;
	
	private UpdateChannelPointsContext updateChannelPointsContext;
	private UpdateStreamInfo updateStreamInfo;
	
	private MessageHandler<ClaimAvailable> claimAvailableHandler;
	
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
			@NotNull ScheduledExecutorService scheduledExecutor,
			@NotNull ExecutorService handlerExecutor){
		this.configuration = configuration;
		this.passportApi = passportApi;
		this.streamerSettingsFactory = streamerSettingsFactory;
		this.webSocketPool = webSocketPool;
		this.scheduledExecutor = scheduledExecutor;
		this.handlerExecutor = handlerExecutor;
		
		streamers = new HashSet<>();
	}
	
	/**
	 * Initialize everything and starts the miner.
	 *
	 * @throws IllegalStateException If the login failed.
	 */
	public void start(){
		log.info("Starting miner");
		webSocketPool.addListener(this);
		webSocketPool.addListener(EventLoggerFactory.create(this));
		
		login();
		loadStreamersFromConfiguration();
		loadStreamersFromFollows();
		
		scheduledExecutor.scheduleWithFixedDelay(getUpdateChannelPointsContext(), 0, 30, MINUTES);
		scheduledExecutor.scheduleWithFixedDelay(getUpdateStreamInfo(), 0, 10, MINUTES);
		scheduledExecutor.scheduleWithFixedDelay(MinerRunnableFactory.createSendMinutesWatched(this), 0, 1, MINUTES);
		scheduledExecutor.scheduleAtFixedRate(MinerRunnableFactory.createWebSocketPing(this), 25, 25, SECONDS);
		
		listenTopic(COMMUNITY_POINTS_USER_V1, getTwitchLogin().fetchUserId());
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
					return new Streamer(user.getId(), streamer.getUsername(), streamerSettingsFactory.createStreamerSettings());
				})
				.filter(Objects::nonNull)
				.forEach(this::addStreamer);
	}
	
	private void loadStreamersFromFollows(){
		if(configuration.isLoadFollows()){
			log.info("Loading streamers from follow list");
			krakenApi.getFollows().stream()
					.filter(follow -> !hasStreamerWithUsername(follow.getChannel().getName()))
					.map(follow -> new Streamer(follow.getChannel().getId(), follow.getChannel().getName(), streamerSettingsFactory.createStreamerSettings()))
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
			gqlApi = ApiFactory.createGqlApi(twitchLogin);
			helixApi = ApiFactory.createHelixApi(twitchLogin);
			krakenApi = ApiFactory.createKrakenApi(twitchLogin);
			twitchApi = ApiFactory.createTwitchApi();
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
			listenTopic(PREDICTIONS_USER_V1, getTwitchLogin().fetchUserId());
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
	
	@Override
	public void onTwitchMessage(@NotNull Topic topic, @NotNull Message message){
		handlerExecutor.submit(() -> handleMessage(topic, message));
	}
	
	private void handleMessage(@NotNull Topic topic, @NotNull Message message){
		if(message instanceof ClaimAvailable claimAvailable){
			getClaimAvailableHandler().handle(topic, claimAvailable);
		}
	}
	
	private MessageHandler<ClaimAvailable> getClaimAvailableHandler(){
		if(Objects.isNull(claimAvailableHandler)){
			claimAvailableHandler = MessageHandlerFactory.createClaimAvailableHandler(this);
		}
		return claimAvailableHandler;
	}
	
	private UpdateChannelPointsContext getUpdateChannelPointsContext(){
		if(Objects.isNull(updateChannelPointsContext)){
			updateChannelPointsContext = MinerRunnableFactory.createUpdateChannelPointsContext(this);
		}
		return updateChannelPointsContext;
	}
	
	private UpdateStreamInfo getUpdateStreamInfo(){
		if(Objects.isNull(updateChannelPointsContext)){
			updateStreamInfo = MinerRunnableFactory.createUpdateStreamInfo(this);
		}
		return updateStreamInfo;
	}
	
	@Override
	public void close(){
		scheduledExecutor.shutdown();
		handlerExecutor.shutdown();
		webSocketPool.close();
	}
}
