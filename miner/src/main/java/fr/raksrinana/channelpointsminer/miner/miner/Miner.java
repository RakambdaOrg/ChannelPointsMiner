package fr.raksrinana.channelpointsminer.miner.miner;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.raksrinana.channelpointsminer.miner.api.chat.TwitchChatFactory;
import fr.raksrinana.channelpointsminer.miner.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.PassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.CaptchaSolveRequired;
import fr.raksrinana.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.miner.api.ws.ITwitchPubSubMessageListener;
import fr.raksrinana.channelpointsminer.miner.api.ws.TwitchPubSubWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.IMessage;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topics;
import fr.raksrinana.channelpointsminer.miner.config.AccountConfiguration;
import fr.raksrinana.channelpointsminer.miner.event.IEvent;
import fr.raksrinana.channelpointsminer.miner.event.IEventListener;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamerAddedEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamerRemovedEvent;
import fr.raksrinana.channelpointsminer.miner.factory.ApiFactory;
import fr.raksrinana.channelpointsminer.miner.factory.MinerRunnableFactory;
import fr.raksrinana.channelpointsminer.miner.factory.StreamerSettingsFactory;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.handler.IMessageHandler;
import fr.raksrinana.channelpointsminer.miner.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.runnable.SyncInventory;
import fr.raksrinana.channelpointsminer.miner.runnable.UpdateStreamInfo;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import lombok.AccessLevel;
import lombok.Getter;
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
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.COMMUNITY_POINTS_USER_V1;
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.ONSITE_NOTIFICATIONS;
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.PREDICTIONS_CHANNEL_V1;
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.PREDICTIONS_USER_V1;
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.RAID;
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.VIDEO_PLAYBACK_BY_ID;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class Miner implements AutoCloseable, IMiner, ITwitchPubSubMessageListener{
	private final AccountConfiguration accountConfiguration;
	private final PassportApi passportApi;
	
	private final Map<String, Streamer> streamers;
	@Getter
	private final TwitchPubSubWebSocketPool pubSubWebSocketPool;
	private final ScheduledExecutorService scheduledExecutor;
	private final ExecutorService handlerExecutor;
	private final StreamerSettingsFactory streamerSettingsFactory;
	@Getter(value = AccessLevel.PUBLIC, onMethod_ = {
			@TestOnly,
			@VisibleForTesting
	})
	private final Collection<IMessageHandler> messageHandlers;
	@Getter(value = AccessLevel.PUBLIC, onMethod_ = {
			@TestOnly,
			@VisibleForTesting
	})
	private final Collection<IEventListener> eventListeners;
	@Getter
	private final MinerData minerData;
	
	private UpdateStreamInfo updateStreamInfo;
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
			@NotNull PassportApi passportApi,
			@NotNull StreamerSettingsFactory streamerSettingsFactory,
			@NotNull TwitchPubSubWebSocketPool pubSubWebSocketPool,
			@NotNull ScheduledExecutorService scheduledExecutor,
			@NotNull ExecutorService handlerExecutor){
		this.accountConfiguration = accountConfiguration;
		this.passportApi = passportApi;
		this.streamerSettingsFactory = streamerSettingsFactory;
		this.pubSubWebSocketPool = pubSubWebSocketPool;
		this.scheduledExecutor = scheduledExecutor;
		this.handlerExecutor = handlerExecutor;
		
		streamers = new ConcurrentHashMap<>();
		messageHandlers = new ConcurrentLinkedQueue<>();
		eventListeners = new ConcurrentLinkedQueue<>();
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
			scheduledExecutor.scheduleWithFixedDelay(MinerRunnableFactory.createSendMinutesWatched(this), 0, 1, MINUTES);
			scheduledExecutor.scheduleAtFixedRate(MinerRunnableFactory.createWebSocketPing(this), 25, 25, SECONDS);
			scheduledExecutor.scheduleAtFixedRate(getSyncInventory(), 1, 15, MINUTES);
			
			var streamerConfigurationReload = MinerRunnableFactory.createStreamerConfigurationReload(this, streamerSettingsFactory, accountConfiguration.isLoadFollows());
			if(accountConfiguration.getReloadEvery() > 0){
				scheduledExecutor.scheduleWithFixedDelay(streamerConfigurationReload, 0, accountConfiguration.getReloadEvery(), MINUTES);
			}
			else{
				scheduledExecutor.schedule(streamerConfigurationReload, 0, MINUTES);
			}
			
			listenTopic(COMMUNITY_POINTS_USER_V1, getTwitchLogin().fetchUserId());
			listenTopic(ONSITE_NOTIFICATIONS, getTwitchLogin().fetchUserId());
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
			twitchApi = ApiFactory.createTwitchApi();
			chatClient = TwitchChatFactory.createChat(accountConfiguration.getChatMode(), twitchLogin);
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
	
	@NotNull
	private SyncInventory getSyncInventory(){
		if(Objects.isNull(syncInventory)){
			syncInventory = MinerRunnableFactory.createSyncInventory(this);
		}
		return syncInventory;
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
			onEvent(new StreamerAddedEvent(this, streamer, TimeFactory.now()));
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
				listenTopic(PREDICTIONS_USER_V1, getTwitchLogin().fetchUserId());
				listenTopic(PREDICTIONS_CHANNEL_V1, streamer.getId());
			}
			else{
				removeTopic(PREDICTIONS_CHANNEL_V1, streamer.getId());
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
			removeTopic(RAID, streamer.getId());
			chatClient.leave(streamer.getUsername());
			
			onEvent(new StreamerRemovedEvent(this, streamer, TimeFactory.now()));
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
	
	@Override
	public void onEvent(IEvent event){
		var values = ThreadContext.getImmutableContext();
		var messages = ThreadContext.getImmutableStack().asList();
		
		eventListeners.forEach(listener -> handlerExecutor.submit(() -> {
			try(var ignored = LogContext.restore(values, messages)){
				listener.onEvent(event);
			}
		}));
	}
	
	private void removeTopic(@NotNull TopicName name, @NotNull String target){
		pubSubWebSocketPool.removeTopic(Topic.builder().name(name).target(target).build());
	}
	
	@Override
	public void onTwitchMessage(@NotNull Topic topic, @NotNull IMessage message){
		var values = ThreadContext.getImmutableContext();
		var messages = ThreadContext.getImmutableStack().asList();
		
		messageHandlers.forEach(handler -> handlerExecutor.submit(() -> {
			try(var ignored = LogContext.restore(values, messages)){
				handler.handle(topic, message);
			}
		}));
	}
	
	public void addHandler(@NotNull IMessageHandler handler){
		messageHandlers.add(handler);
	}
	
	public void addEventListener(@NotNull IEventListener listener){
		eventListeners.add(listener);
	}
	
	@Override
	public void close(){
		scheduledExecutor.shutdown();
		handlerExecutor.shutdown();
		pubSubWebSocketPool.close();
		if(!Objects.isNull(chatClient)){
			chatClient.close();
		}
		for(var listener : eventListeners){
			listener.close();
		}
	}
	
	@NotNull
	@VisibleForTesting
	@TestOnly
	protected Map<String, Streamer> getStreamerMap(){
		return streamers;
	}
}
