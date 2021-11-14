package fr.raksrinana.channelpointsminer.miner;

import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.passport.PassportApi;
import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.api.passport.exceptions.CaptchaSolveRequired;
import fr.raksrinana.channelpointsminer.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.api.ws.ITwitchMessageListener;
import fr.raksrinana.channelpointsminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.channelpointsminer.api.ws.data.message.IMessage;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.TopicName;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.channelpointsminer.config.AccountConfiguration;
import fr.raksrinana.channelpointsminer.factory.ApiFactory;
import fr.raksrinana.channelpointsminer.factory.MinerRunnableFactory;
import fr.raksrinana.channelpointsminer.factory.StreamerSettingsFactory;
import fr.raksrinana.channelpointsminer.handler.IMessageHandler;
import fr.raksrinana.channelpointsminer.irc.TwitchIrcClient;
import fr.raksrinana.channelpointsminer.irc.TwitchIrcFactory;
import fr.raksrinana.channelpointsminer.log.ILogEventListener;
import fr.raksrinana.channelpointsminer.log.LogContext;
import fr.raksrinana.channelpointsminer.log.event.ILogEvent;
import fr.raksrinana.channelpointsminer.runnable.UpdateStreamInfo;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.VisibleForTesting;
import java.util.*;
import java.util.concurrent.*;
import static fr.raksrinana.channelpointsminer.api.ws.data.request.topic.TopicName.*;
import static fr.raksrinana.channelpointsminer.factory.MinerRunnableFactory.*;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class Miner implements AutoCloseable, IMiner, ITwitchMessageListener{
	private final AccountConfiguration accountConfiguration;
	private final PassportApi passportApi;
	
	private final Map<String, Streamer> streamers;
	@Getter
	private final TwitchWebSocketPool webSocketPool;
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
	private final Collection<ILogEventListener> logEventListeners;
	@Getter
	private final MinerData minerData;
	
	private UpdateStreamInfo updateStreamInfo;
	
	@Getter
	private TwitchLogin twitchLogin;
	@Getter
	private GQLApi gqlApi;
	@Getter
	private TwitchApi twitchApi;
	private TwitchIrcClient ircClient;
	
	public Miner(@NotNull AccountConfiguration accountConfiguration,
			@NotNull PassportApi passportApi,
			@NotNull StreamerSettingsFactory streamerSettingsFactory,
			@NotNull TwitchWebSocketPool webSocketPool,
			@NotNull ScheduledExecutorService scheduledExecutor,
			@NotNull ExecutorService handlerExecutor){
		this.accountConfiguration = accountConfiguration;
		this.passportApi = passportApi;
		this.streamerSettingsFactory = streamerSettingsFactory;
		this.webSocketPool = webSocketPool;
		this.scheduledExecutor = scheduledExecutor;
		this.handlerExecutor = handlerExecutor;
		
		streamers = new ConcurrentHashMap<>();
		messageHandlers = new LinkedList<>();
		logEventListeners = new LinkedList<>();
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
			webSocketPool.addListener(this);
			
			login();
			
			scheduledExecutor.scheduleWithFixedDelay(getUpdateStreamInfo(), 0, 2, MINUTES);
			scheduledExecutor.scheduleWithFixedDelay(createSendMinutesWatched(this), 0, 1, MINUTES);
			scheduledExecutor.scheduleAtFixedRate(createWebSocketPing(this), 25, 25, SECONDS);
			scheduledExecutor.scheduleAtFixedRate(createSyncInventory(this), 1, 15, MINUTES);
			
			var streamerConfigurationReload = MinerRunnableFactory.createStreamerConfigurationReload(this, streamerSettingsFactory, accountConfiguration.isLoadFollows());
			if(accountConfiguration.getReloadEvery() > 0){
				scheduledExecutor.scheduleWithFixedDelay(streamerConfigurationReload, 0, accountConfiguration.getReloadEvery(), MINUTES);
			}
			else{
				scheduledExecutor.schedule(streamerConfigurationReload, 0, MINUTES);
			}
			
			listenTopic(COMMUNITY_POINTS_USER_V1, getTwitchLogin().fetchUserId());
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
			ircClient = TwitchIrcFactory.create(twitchLogin);
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
	public String getUsername(){
		return accountConfiguration.getUsername();
	}
	
	@NotNull
	@VisibleForTesting
	@TestOnly
	protected Map<String, Streamer> getStreamerMap(){
		return streamers;
	}
	
	@Override
	@NotNull
	public Collection<Streamer> getStreamers(){
		return streamers.values();
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
			
			if(streamer.getSettings().isJoinIrc()){
				ircClient.join(streamer.getUsername());
			}
			else{
				ircClient.leave(streamer.getUsername());
			}
		}
	}
	
	@Override
	public boolean removeStreamer(@NotNull Streamer streamer){
		try(var ignored = LogContext.empty().withStreamer(streamer)){
			removeTopic(VIDEO_PLAYBACK_BY_ID, streamer.getId());
			removeTopic(PREDICTIONS_CHANNEL_V1, streamer.getId());
			removeTopic(RAID, streamer.getId());
			ircClient.leave(streamer.getUsername());
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
	
	private void listenTopic(@NotNull TopicName name, @NotNull String target){
		webSocketPool.listenTopic(Topics.buildFromName(name, target, twitchLogin.getAccessToken()));
	}
	
	private void removeTopic(@NotNull TopicName name, @NotNull String target){
		webSocketPool.removeTopic(Topic.builder().name(name).target(target).build());
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
	
	@Override
	public void onLogEvent(ILogEvent event){
		var values = ThreadContext.getImmutableContext();
		var messages = ThreadContext.getImmutableStack().asList();
		
		logEventListeners.forEach(listener -> handlerExecutor.submit(() -> {
			try(var ignored = LogContext.restore(values, messages)){
				listener.onLogEvent(event);
			}
		}));
	}
	
	private UpdateStreamInfo getUpdateStreamInfo(){
		if(Objects.isNull(updateStreamInfo)){
			updateStreamInfo = MinerRunnableFactory.createUpdateStreamInfo(this);
		}
		return updateStreamInfo;
	}
	
	public void addHandler(@NotNull IMessageHandler handler){
		messageHandlers.add(handler);
	}
	
	public void addLogEventListener(@NotNull ILogEventListener eventListener){
		logEventListeners.add(eventListener);
	}
	
	@Override
	public void close(){
		scheduledExecutor.shutdown();
		handlerExecutor.shutdown();
		webSocketPool.close();
		if(!Objects.isNull(ircClient)){
			ircClient.close();
		}
	}
}
