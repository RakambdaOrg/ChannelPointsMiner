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
import fr.raksrinana.twitchminer.api.ws.data.message.Message;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.twitchminer.config.Configuration;
import fr.raksrinana.twitchminer.factory.ApiFactory;
import fr.raksrinana.twitchminer.factory.MinerRunnableFactory;
import fr.raksrinana.twitchminer.factory.StreamerSettingsFactory;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import fr.raksrinana.twitchminer.miner.handler.MessageHandler;
import fr.raksrinana.twitchminer.miner.runnables.UpdateStreamInfo;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName.*;
import static fr.raksrinana.twitchminer.factory.MinerRunnableFactory.createSendMinutesWatched;
import static fr.raksrinana.twitchminer.factory.MinerRunnableFactory.createWebSocketPing;
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
	private final Collection<MessageHandler> messageHandlers;
	
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
			@NotNull ScheduledExecutorService scheduledExecutor,
			@NotNull ExecutorService handlerExecutor){
		this.configuration = configuration;
		this.passportApi = passportApi;
		this.streamerSettingsFactory = streamerSettingsFactory;
		this.webSocketPool = webSocketPool;
		this.scheduledExecutor = scheduledExecutor;
		this.handlerExecutor = handlerExecutor;
		
		streamers = new HashSet<>();
		messageHandlers = new LinkedList<>();
	}
	
	/**
	 * Initialize everything and starts the miner.
	 *
	 * @throws IllegalStateException If the login failed.
	 */
	public void start(){
		log.info("Starting miner");
		webSocketPool.addListener(this);
		
		login();
		loadStreamersFromConfiguration();
		loadStreamersFromFollows();
		
		scheduledExecutor.scheduleWithFixedDelay(getUpdateStreamInfo(), 0, 2, MINUTES);
		scheduledExecutor.scheduleWithFixedDelay(createSendMinutesWatched(this), 0, 1, MINUTES);
		scheduledExecutor.scheduleAtFixedRate(createWebSocketPing(this), 25, 25, SECONDS);
		
		listenTopic(COMMUNITY_POINTS_USER_V1, getTwitchLogin().fetchUserId());
	}
	
	@SneakyThrows
	private void loadStreamersFromConfiguration(){
		log.info("Loading streamers from configuration");
		Files.list(configuration.getStreamerConfigDirectory())
				.map(Path::getFileName)
				.map(Path::toString)
				.filter(name -> name.endsWith(".json"))
				.map(name -> name.substring(0, name.length() - ".json".length()))
				.map(name -> {
					var user = gqlApi.reportMenuItem(name)
							.map(GQLResponse::getData)
							.map(ReportMenuItemData::getUser)
							.orElse(null);
					if(Objects.isNull(user)){
						log.error("Failed to get streamer " + name);
						return null;
					}
					return new Streamer(user.getId(), name, streamerSettingsFactory.createStreamerSettings(name));
				})
				.filter(Objects::nonNull)
				.forEach(this::addStreamer);
	}
	
	private void loadStreamersFromFollows(){
		if(configuration.isLoadFollows()){
			log.info("Loading streamers from follow list");
			krakenApi.getFollows().stream()
					.filter(follow -> !hasStreamerWithUsername(follow.getChannel().getName()))
					.map(follow -> {
						var streamerId = follow.getChannel().getId();
						var streamerName = follow.getChannel().getName();
						return new Streamer(streamerId, streamerName, streamerSettingsFactory.createStreamerSettings(streamerName));
					})
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
	@NotNull
	public Optional<Streamer> getStreamerById(@NotNull String id){
		return getStreamers().stream()
				.filter(s -> Objects.equals(s.getId(), id))
				.findFirst();
	}
	
	@Override
	public void addStreamer(@NotNull Streamer streamer){
		if(streamers.contains(streamer)){
			log.debug("Streamer {} is already being mined", streamer);
			return;
		}
		log.info("Added to the mining list: {}", streamer);
		
		updateStreamerInfos(streamer);
		
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
	public void updateStreamerInfos(@NotNull Streamer streamer){
		getUpdateStreamInfo().run(streamer);
	}
	
	@Override
	@NotNull
	public ScheduledFuture<?> schedule(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit){
		return scheduledExecutor.schedule(runnable, delay, unit);
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
		messageHandlers.forEach(handler -> handler.handle(topic, message));
	}
	
	private UpdateStreamInfo getUpdateStreamInfo(){
		if(Objects.isNull(updateStreamInfo)){
			updateStreamInfo = MinerRunnableFactory.createUpdateStreamInfo(this);
		}
		return updateStreamInfo;
	}
	
	public void addHandler(MessageHandler handler){
		messageHandlers.add(handler);
	}
	
	@Override
	public void close(){
		scheduledExecutor.shutdown();
		handlerExecutor.shutdown();
		webSocketPool.close();
	}
}
