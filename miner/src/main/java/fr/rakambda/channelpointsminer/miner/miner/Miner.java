package fr.rakambda.channelpointsminer.miner.miner;

import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.rakambda.channelpointsminer.miner.api.chat.TwitchChatEventProducer;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.rakambda.channelpointsminer.miner.api.passport.ILoginProvider;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.CaptchaSolveRequired;
import fr.rakambda.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.rakambda.channelpointsminer.miner.api.pubsub.ITwitchPubSubMessageListener;
import fr.rakambda.channelpointsminer.miner.api.pubsub.TwitchPubSubWebSocketPool;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topics;
import fr.rakambda.channelpointsminer.miner.config.AccountConfiguration;
import fr.rakambda.channelpointsminer.miner.database.IDatabase;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerAddedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerRemovedEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.ApiFactory;
import fr.rakambda.channelpointsminer.miner.factory.MinerRunnableFactory;
import fr.rakambda.channelpointsminer.miner.factory.StreamerSettingsFactory;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.factory.TwitchChatFactory;
import fr.rakambda.channelpointsminer.miner.handler.IPubSubMessageHandler;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.runnable.SyncInventory;
import fr.rakambda.channelpointsminer.miner.runnable.UpdateStreamInfo;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.COMMUNITY_MOMENTS_CHANNEL_V1;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.COMMUNITY_POINTS_USER_V1;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.ONSITE_NOTIFICATIONS;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.PREDICTIONS_CHANNEL_V1;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.PREDICTIONS_USER_V1;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.RAID;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.USER_DROP_EVENTS;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.VIDEO_PLAYBACK_BY_ID;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class Miner implements AutoCloseable, IMiner, ITwitchPubSubMessageListener{
	private final AccountConfiguration accountConfiguration;
	private final ILoginProvider passportApi;
	
	private final Map<String, Streamer> streamers;
	@Getter
	private final TwitchPubSubWebSocketPool pubSubWebSocketPool;
	private final ScheduledExecutorService scheduledExecutor;
	private final ExecutorService handlerExecutor;
	@Getter
	private final IDatabase database;
	private final StreamerSettingsFactory streamerSettingsFactory;
	@Getter(value = AccessLevel.PUBLIC, onMethod_ = {
			@TestOnly,
			@VisibleForTesting
	})
	private final Collection<IPubSubMessageHandler> pubSubMessageHandlers;
	private final IEventManager eventManager;
	@Getter
	private final MinerData minerData;
	
	private UpdateStreamInfo updateStreamInfo;
	@Setter
	@Getter(value = AccessLevel.PUBLIC, onMethod_ = {@TestOnly})
	private SyncInventory syncInventory;
	
	@Getter
	private TwitchLogin twitchLogin;
	@Getter
	private GQLApi gqlApi;
	@Getter
	private TwitchApi twitchApi;
	@Getter
	private ITwitchChatClient chatClient;
	
	public Miner(@NotNull AccountConfiguration accountConfiguration,
			@NotNull ILoginProvider passportApi,
			@NotNull StreamerSettingsFactory streamerSettingsFactory,
			@NotNull TwitchPubSubWebSocketPool pubSubWebSocketPool,
			@NotNull ScheduledExecutorService scheduledExecutor,
			@NotNull ExecutorService handlerExecutor,
			@NotNull IDatabase database,
			@NotNull IEventManager eventManager){
		this.accountConfiguration = accountConfiguration;
		this.passportApi = passportApi;
		this.streamerSettingsFactory = streamerSettingsFactory;
		this.pubSubWebSocketPool = pubSubWebSocketPool;
		this.scheduledExecutor = scheduledExecutor;
		this.handlerExecutor = handlerExecutor;
		this.database = database;
		this.eventManager = eventManager;
		
		streamers = new ConcurrentHashMap<>();
		pubSubMessageHandlers = new ConcurrentLinkedQueue<>();
		minerData = new MinerData();
	}
	
	/**
	 * Initialize everything and starts the miner.
	 *
	 * @throws IllegalStateException If the login failed.
	 */
	public void start(){
		try(var ignored = LogContext.with(this)){
			log.info("Starting miner");
			pubSubWebSocketPool.addListener(this);
			
			login();
			
			scheduledExecutor.scheduleWithFixedDelay(getUpdateStreamInfo(), 0, 2, MINUTES);
			scheduledExecutor.scheduleWithFixedDelay(MinerRunnableFactory.createSendSpadeMinutesWatched(this), 0, 1, MINUTES);
			scheduledExecutor.scheduleWithFixedDelay(MinerRunnableFactory.createSendM3u8MinutesWatched(this), 0, 15, SECONDS);
			scheduledExecutor.scheduleAtFixedRate(MinerRunnableFactory.createWebSocketPing(this), 25, 25, SECONDS);
			scheduledExecutor.scheduleAtFixedRate(syncInventory, 1, 15, MINUTES);
			
			var streamerConfigurationReload = MinerRunnableFactory.createStreamerConfigurationReload(this, eventManager, streamerSettingsFactory, accountConfiguration.isLoadFollows());
			if(accountConfiguration.getReloadEvery() > 0){
				scheduledExecutor.scheduleWithFixedDelay(streamerConfigurationReload, 0, accountConfiguration.getReloadEvery(), MINUTES);
			}
			else{
				scheduledExecutor.schedule(streamerConfigurationReload, 0, MINUTES);
			}
			
			var connectedUserId = getTwitchLogin().fetchUserId(gqlApi);
			listenTopic(COMMUNITY_POINTS_USER_V1, connectedUserId);
			listenTopic(USER_DROP_EVENTS, connectedUserId);
			listenTopic(ONSITE_NOTIFICATIONS, connectedUserId);
		}
	}
	
	/**
	 * Login to twitch.
	 *
	 * @throws IllegalStateException If the login failed.
	 */
	private void login(){
		try{
			var analyticsConfiguration = accountConfiguration.getAnalytics();
			var listenMessages = analyticsConfiguration.isEnabled() && analyticsConfiguration.isRecordUserPredictions();
			
			twitchLogin = passportApi.login();
			
			if(!accountConfiguration.getUsername().equalsIgnoreCase(twitchLogin.getUsername())){
				throw new IllegalStateException("Failed to log in, expected account %s but was %s".formatted(accountConfiguration.getUsername(), twitchLogin.getUsername()));
			}
			
			var versionProvider = ApiFactory.createVersionProvider(accountConfiguration.getVersionProvider());
			var integrityProvider = ApiFactory.createIntegrityProvider(twitchLogin, versionProvider, accountConfiguration.getLoginMethod(), eventManager);
			gqlApi = ApiFactory.createGqlApi(twitchLogin, integrityProvider);
			twitchApi = ApiFactory.createTwitchApi(twitchLogin);
			chatClient = TwitchChatFactory.createChat(this, accountConfiguration.getChatMode(), listenMessages);
			chatClient.addChatMessageListener(new TwitchChatEventProducer(eventManager));
		}
		catch(CaptchaSolveRequired e){
			throw new IllegalStateException("A captcha solve is required, please log in through your browser and solve it");
		}
		catch(Exception e){
			throw new IllegalStateException("Failed to login", e);
		}
	}
	
	@NotNull
	private UpdateStreamInfo getUpdateStreamInfo(){
		if(Objects.isNull(updateStreamInfo)){
			updateStreamInfo = MinerRunnableFactory.createUpdateStreamInfo(this);
		}
		return updateStreamInfo;
	}
	
	private void listenTopic(@NotNull TopicName name, @NotNull String target){
		pubSubWebSocketPool.listenTopic(Topics.buildFromName(name, target, twitchLogin.getAccessToken()));
	}
	
	@Override
	@NotNull
	public Optional<Streamer> getStreamerById(@NotNull String id){
		return Optional.ofNullable(streamers.get(id));
	}
	
	@Override
	public void addStreamer(@NotNull Streamer streamer){
		try(var ignored = LogContext.empty().withStreamer(streamer)){
			if(containsStreamer(streamer)){
				log.debug("Streamer is already being mined");
				return;
			}
			log.info("Adding to the mining list with settings {}", streamer.getSettings());
			updateStreamerInfos(streamer);
			
			streamers.put(streamer.getId(), streamer);
			eventManager.onEvent(new StreamerAddedEvent(streamer, TimeFactory.now()));
			updateStreamer(streamer);
		}
	}
	
	@Override
	public void updateStreamer(@NotNull Streamer streamer){
		try(var ignored = LogContext.empty().withStreamer(streamer)){
			if(!containsStreamer(streamer)){
				log.debug("Streamer is can't be updated as it is unknown");
				return;
			}
			
			listenTopic(VIDEO_PLAYBACK_BY_ID, streamer.getId());
			
			if(streamer.getSettings().isMakePredictions()){
				listenTopic(PREDICTIONS_USER_V1, getTwitchLogin().fetchUserId(gqlApi));
				listenTopic(PREDICTIONS_CHANNEL_V1, streamer.getId());
			}
			else{
				removeTopic(PREDICTIONS_CHANNEL_V1, streamer.getId());
			}
			
			if(streamer.getSettings().isClaimMoments()){
				listenTopic(COMMUNITY_MOMENTS_CHANNEL_V1, streamer.getId());
			}
			else{
				removeTopic(COMMUNITY_MOMENTS_CHANNEL_V1, streamer.getId());
			}
			
			if(streamer.getSettings().isFollowRaid()){
				listenTopic(RAID, streamer.getId());
			}
			else{
				removeTopic(RAID, streamer.getId());
			}
			
			if(streamer.isStreaming() && streamer.getSettings().isJoinIrc()){
				getChatClient().join(streamer.getUsername());
			}
			else{
				getChatClient().leave(streamer.getUsername());
			}
		}
	}
	
	@Override
	public boolean removeStreamer(@NotNull Streamer streamer){
		try(var ignored = LogContext.empty().withStreamer(streamer)){
			if(!containsStreamer(streamer)){
				log.debug("Can't remove streamer as it isn't in the mining list");
				return false;
			}
			log.info("Removing streamer from the mining list");
			removeTopic(VIDEO_PLAYBACK_BY_ID, streamer.getId());
			removeTopic(PREDICTIONS_CHANNEL_V1, streamer.getId());
			removeTopic(COMMUNITY_MOMENTS_CHANNEL_V1, streamer.getId());
			removeTopic(RAID, streamer.getId());
			chatClient.leave(streamer.getUsername());
			
			eventManager.onEvent(new StreamerRemovedEvent(streamer, TimeFactory.now()));
			return streamers.remove(streamer.getId()) != null;
		}
	}
	
	@Override
	public void updateStreamerInfos(@NotNull Streamer streamer){
		try(var ignored = LogContext.empty().withStreamer(streamer)){
			getUpdateStreamInfo().run(streamer);
		}
	}
	
	@Override
	public void syncInventory(){
		getSyncInventory().run();
	}
	
	@Override
	public boolean containsStreamer(@NotNull Streamer streamer){
		return streamers.containsKey(streamer.getId());
	}
	
	@Override
	@NotNull
	public ScheduledFuture<?> schedule(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit){
		var values = ThreadContext.getImmutableContext();
		var messages = ThreadContext.getImmutableStack().asList();
		
		Runnable contextSharing = () -> {
			try(var ignored = LogContext.restore(values, messages)){
				runnable.run();
			}
		};
		
		return scheduledExecutor.schedule(contextSharing, delay, unit);
	}
	
	@Override
	@NotNull
	public Collection<Streamer> getStreamers(){
		return streamers.values();
	}
	
	@Override
	@NotNull
	public String getUsername(){
		return accountConfiguration.getUsername();
	}
	
	private void removeTopic(@NotNull TopicName name, @NotNull String target){
		pubSubWebSocketPool.removeTopic(Topic.builder().name(name).target(target).build());
	}
	
	@Override
	public void onTwitchMessage(@NotNull Topic topic, @NotNull IPubSubMessage message){
		var values = ThreadContext.getImmutableContext();
		var messages = ThreadContext.getImmutableStack().asList();
		
		pubSubMessageHandlers.forEach(handler -> handlerExecutor.submit(() -> {
			try(var ignored = LogContext.restore(values, messages)){
				handler.handle(topic, message);
			}
		}));
	}
	
	public void addPubSubHandler(@NotNull IPubSubMessageHandler handler){
		pubSubMessageHandlers.add(handler);
	}
	
	@Override
	public void close(){
		scheduledExecutor.shutdown();
		handlerExecutor.shutdown();
		pubSubWebSocketPool.close();
		if(!Objects.isNull(chatClient)){
			chatClient.close();
		}
		eventManager.close();
	}
	
	@NotNull
	@VisibleForTesting
	@TestOnly
	protected Map<String, Streamer> getStreamerMap(){
		return streamers;
	}
}