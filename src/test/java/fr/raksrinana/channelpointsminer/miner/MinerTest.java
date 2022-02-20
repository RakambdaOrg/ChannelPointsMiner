package fr.raksrinana.channelpointsminer.miner;

import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.passport.PassportApi;
import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.api.passport.exceptions.CaptchaSolveRequired;
import fr.raksrinana.channelpointsminer.api.passport.exceptions.LoginException;
import fr.raksrinana.channelpointsminer.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.channelpointsminer.api.ws.data.message.IMessage;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.channelpointsminer.config.AccountConfiguration;
import fr.raksrinana.channelpointsminer.event.IEvent;
import fr.raksrinana.channelpointsminer.event.IEventListener;
import fr.raksrinana.channelpointsminer.event.impl.StreamerAddedEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamerRemovedEvent;
import fr.raksrinana.channelpointsminer.factory.ApiFactory;
import fr.raksrinana.channelpointsminer.factory.MinerRunnableFactory;
import fr.raksrinana.channelpointsminer.factory.StreamerSettingsFactory;
import fr.raksrinana.channelpointsminer.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.handler.IMessageHandler;
import fr.raksrinana.channelpointsminer.irc.TwitchIrcClient;
import fr.raksrinana.channelpointsminer.irc.TwitchIrcFactory;
import fr.raksrinana.channelpointsminer.runnable.StreamerConfigurationReload;
import fr.raksrinana.channelpointsminer.runnable.UpdateStreamInfo;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import fr.raksrinana.channelpointsminer.streamer.StreamerSettings;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import static fr.raksrinana.channelpointsminer.api.ws.data.request.topic.TopicName.COMMUNITY_POINTS_USER_V1;
import static fr.raksrinana.channelpointsminer.api.ws.data.request.topic.TopicName.PREDICTIONS_CHANNEL_V1;
import static fr.raksrinana.channelpointsminer.api.ws.data.request.topic.TopicName.PREDICTIONS_USER_V1;
import static fr.raksrinana.channelpointsminer.api.ws.data.request.topic.TopicName.RAID;
import static fr.raksrinana.channelpointsminer.api.ws.data.request.topic.TopicName.VIDEO_PLAYBACK_BY_ID;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinerTest{
	private static final String STREAMER_USERNAME = "streamer-username";
	private static final String STREAMER_ID = "streamer-id";
	private static final String USER_ID = "user-id";
	private static final String ACCESS_TOKEN = "access-token";
	private static final Instant NOW = Instant.parse("2020-05-17T12:14:20.000Z");
	
	private Miner tested;
	
	@Mock
	private AccountConfiguration accountConfiguration;
	@Mock
	private PassportApi passportApi;
	@Mock
	private TwitchWebSocketPool webSocketPool;
	@Mock
	private StreamerSettingsFactory streamerSettingsFactory;
	@Mock
	private ScheduledExecutorService scheduledExecutorService;
	@Mock
	private ExecutorService executorService;
	
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private StreamerSettings streamerSettings;
	@Mock
	private TwitchApi twitchApi;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private UpdateStreamInfo updateStreamInfo;
	@Mock
	private Topic topic;
	@Mock
	private TwitchIrcClient twitchIrcClient;
	@Mock
	private StreamerConfigurationReload streamerConfigurationReload;
	@Mock
	private IEventListener eventListener;
	
	@BeforeEach
	void setUp() throws LoginException, IOException{
		tested = new Miner(accountConfiguration, passportApi, streamerSettingsFactory, webSocketPool, scheduledExecutorService, executorService);
		
		lenient().when(accountConfiguration.getReloadEvery()).thenReturn(0);
		lenient().when(accountConfiguration.isLoadFollows()).thenReturn(false);
		
		lenient().when(passportApi.login()).thenReturn(twitchLogin);
		lenient().when(streamerSettings.isFollowRaid()).thenReturn(false);
		lenient().when(streamerSettings.isMakePredictions()).thenReturn(false);
		lenient().when(streamerSettings.isJoinIrc()).thenReturn(false);
		lenient().when(twitchLogin.fetchUserId()).thenReturn(USER_ID);
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
		
		lenient().when(executorService.submit(any(Runnable.class))).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return CompletableFuture.completedFuture(null);
		});
	}
	
	@Test
	void setupIsDoneWithNoConfigReload() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createStreamerConfigurationReload(tested, streamerSettingsFactory, false)).thenReturn(streamerConfigurationReload);
			
			assertDoesNotThrow(() -> tested.start());
			
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getIrcClient()).isEqualTo(twitchIrcClient);
			assertThat(tested.getStreamers()).isEmpty();
			
			verify(passportApi).login();
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient, never()).join(any());
			verify(scheduledExecutorService).schedule(eq(streamerConfigurationReload), anyLong(), any());
		}
	}
	
	@Test
	void setupIsDoneWithConfigReload() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createStreamerConfigurationReload(tested, streamerSettingsFactory, true)).thenReturn(streamerConfigurationReload);
			
			lenient().when(accountConfiguration.getReloadEvery()).thenReturn(15);
			lenient().when(accountConfiguration.isLoadFollows()).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.start());
			
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).isEmpty();
			
			verify(passportApi).login();
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(scheduledExecutorService).scheduleWithFixedDelay(eq(streamerConfigurationReload), anyLong(), eq(15L), eq(MINUTES));
		}
	}
	
	@Test
	void setupIsDoneWithConfigReloadAndFollows() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createStreamerConfigurationReload(tested, streamerSettingsFactory, false)).thenReturn(streamerConfigurationReload);
			
			lenient().when(accountConfiguration.getReloadEvery()).thenReturn(15);
			
			assertDoesNotThrow(() -> tested.start());
			
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).isEmpty();
			
			verify(passportApi).login();
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(scheduledExecutorService).scheduleWithFixedDelay(eq(streamerConfigurationReload), anyLong(), eq(15L), eq(MINUTES));
		}
	}
	
	@Test
	void captchaLogin() throws LoginException, IOException{
		when(passportApi.login()).thenThrow(new CaptchaSolveRequired(400, -1, "For tests"));
		
		assertThrows(IllegalStateException.class, () -> tested.start());
	}
	
	@Test
	void exceptionLogin() throws LoginException, IOException{
		when(passportApi.login()).thenThrow(new RuntimeException("For tests"));
		
		assertThrows(IllegalStateException.class, () -> tested.start());
	}
	
	@Test
	void close() throws Exception{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			tested.addEventListener(eventListener);
			tested.start();
			
			assertDoesNotThrow(() -> tested.close());
			
			verify(scheduledExecutorService).shutdown();
			verify(executorService).shutdown();
			verify(webSocketPool).close();
			verify(twitchIrcClient).close();
			verify(eventListener).close();
		}
	}
	
	@Test
	void unknownMessageIsNotForwarded(){
		var message = mock(IMessage.class);
		assertDoesNotThrow(() -> tested.onTwitchMessage(topic, message));
		
		verify(executorService, never()).submit(any(Runnable.class));
	}
	
	@Test
	void messageHandlersAreCalled(){
		var handler1 = mock(IMessageHandler.class);
		var handler2 = mock(IMessageHandler.class);
		
		tested.addHandler(handler1);
		tested.addHandler(handler2);
		
		var message = mock(IMessage.class);
		assertDoesNotThrow(() -> tested.onTwitchMessage(topic, message));
		
		verify(executorService, times(2)).submit(any(Runnable.class));
		verify(handler1).handle(topic, message);
		verify(handler2).handle(topic, message);
	}
	
	@Test
	void addStreamerWithPredictions(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamerSettings.isMakePredictions()).thenReturn(true);
			
			tested.addEventListener(eventListener);
			tested.start();
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(streamer);
			
			verify(updateStreamInfo).run(streamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_CHANNEL_V1, STREAMER_ID, ACCESS_TOKEN));
			verify(eventListener).onEvent(new StreamerAddedEvent(tested, streamer, NOW));
		}
	}
	
	@Test
	void addStreamerWithRaid(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamerSettings.isFollowRaid()).thenReturn(true);
			
			tested.addEventListener(eventListener);
			tested.start();
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(streamer);
			
			verify(updateStreamInfo).run(streamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(RAID, STREAMER_ID, ACCESS_TOKEN));
			verify(eventListener).onEvent(new StreamerAddedEvent(tested, streamer, NOW));
		}
	}
	
	@Test
	void addStreamerWithIrcAndStreamerOffline(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			lenient().when(streamerSettings.isJoinIrc()).thenReturn(true);
			
			tested.addEventListener(eventListener);
			tested.start();
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(streamer);
			
			verify(updateStreamInfo).run(streamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(eventListener).onEvent(new StreamerAddedEvent(tested, streamer, NOW));
			verify(twitchIrcClient, never()).join(any());
		}
	}
	
	@Test
	void addStreamerWithIrcAndStreamerOnline(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			lenient().when(streamerSettings.isJoinIrc()).thenReturn(true);
			
			tested.addEventListener(eventListener);
			tested.start();
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(streamer);
			
			verify(updateStreamInfo).run(streamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(eventListener).onEvent(new StreamerAddedEvent(tested, streamer, NOW));
			verify(twitchIrcClient).join(STREAMER_USERNAME);
		}
	}
	
	@Test
	void addDuplicateStreamer(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			tested.addEventListener(eventListener);
			tested.start();
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(streamer);
			
			verify(updateStreamInfo).run(streamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(eventListener).onEvent(new StreamerAddedEvent(tested, streamer, NOW));
		}
	}
	
	@Test
	void getStreamerByIdUnknown(){
		assertThat(tested.getStreamerById("unknown")).isEmpty();
	}
	
	@Test
	void schedule(){
		var future = mock(ScheduledFuture.class);
		var called = new AtomicBoolean(false);
		Runnable runnable = () -> called.set(true);
		
		when(scheduledExecutorService.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
			var arg = invocation.getArgument(0, Runnable.class);
			arg.run();
			return future;
		});
		
		assertNotNull(tested.schedule(runnable, 1, SECONDS));
		assertThat(called.get()).isTrue();
	}
	
	@Test
	void getStreamerById(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			var id1 = "ID1";
			var id2 = "ID2";
			
			var streamer1 = mock(Streamer.class);
			var streamer2 = mock(Streamer.class);
			when(streamer1.getId()).thenReturn(id1);
			when(streamer2.getId()).thenReturn(id2);
			when(streamer1.getSettings()).thenReturn(streamerSettings);
			when(streamer2.getSettings()).thenReturn(streamerSettings);
			
			tested.start();
			tested.addStreamer(streamer1);
			tested.addStreamer(streamer2);
			
			assertThat(tested.getStreamerById(id1)).isPresent()
					.get().isEqualTo(streamer1);
			assertThat(tested.getStreamerById(id2)).isPresent()
					.get().isEqualTo(streamer2);
		}
	}
	
	@Test
	void removeStreamer(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			tested.getStreamerMap().put(STREAMER_ID, streamer);
			tested.addEventListener(eventListener);
			
			tested.start();
			tested.removeStreamer(streamer);
			
			verify(webSocketPool).removeTopic(Topic.builder().name(VIDEO_PLAYBACK_BY_ID).target(STREAMER_ID).build());
			verify(webSocketPool).removeTopic(Topic.builder().name(PREDICTIONS_CHANNEL_V1).target(STREAMER_ID).build());
			verify(webSocketPool).removeTopic(Topic.builder().name(RAID).target(STREAMER_ID).build());
			verify(twitchIrcClient).leave(STREAMER_USERNAME);
			verify(eventListener).onEvent(new StreamerRemovedEvent(tested, streamer, NOW));
		}
	}
	
	@Test
	void removeUnknownStreamer(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			tested.addEventListener(eventListener);
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			
			tested.start();
			tested.removeStreamer(streamer);
			
			verify(webSocketPool, never()).removeTopic(any());
			verify(twitchIrcClient, never()).leave(any());
			verify(eventListener, never()).onEvent(any());
		}
	}
	
	@Test
	void updateUnknownStreamer(){
		var streamer = mock(Streamer.class);
		when(streamer.getId()).thenReturn(STREAMER_ID);
		when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
		
		assertDoesNotThrow(() -> tested.updateStreamer(streamer));
		
		verify(webSocketPool, never()).listenTopic(any());
		verify(webSocketPool, never()).removeTopic(any());
	}
	
	@Test
	void updateStreamerAllActivatedOnline(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			tested.start();
			
			when(streamerSettings.isMakePredictions()).thenReturn(true);
			when(streamerSettings.isFollowRaid()).thenReturn(true);
			when(streamerSettings.isJoinIrc()).thenReturn(true);
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(true);
			
			tested.getStreamerMap().put(STREAMER_ID, streamer);
			
			assertDoesNotThrow(() -> tested.updateStreamer(streamer));
			
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_CHANNEL_V1, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(RAID, STREAMER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient).join(STREAMER_USERNAME);
		}
	}
	
	@Test
	void updateStreamerAllActivatedOffline(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			tested.start();
			
			when(streamerSettings.isMakePredictions()).thenReturn(true);
			when(streamerSettings.isFollowRaid()).thenReturn(true);
			lenient().when(streamerSettings.isJoinIrc()).thenReturn(true);
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(false);
			
			tested.getStreamerMap().put(STREAMER_ID, streamer);
			
			assertDoesNotThrow(() -> tested.updateStreamer(streamer));
			
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_CHANNEL_V1, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(RAID, STREAMER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient, never()).join(any());
		}
	}
	
	@Test
	void updateStreamerNothingActivatedOnline(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			tested.start();
			
			when(streamerSettings.isMakePredictions()).thenReturn(false);
			when(streamerSettings.isFollowRaid()).thenReturn(false);
			when(streamerSettings.isJoinIrc()).thenReturn(false);
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(true);
			
			tested.getStreamerMap().put(STREAMER_ID, streamer);
			
			assertDoesNotThrow(() -> tested.updateStreamer(streamer));
			
			verify(webSocketPool).removeTopic(Topic.builder().name(PREDICTIONS_CHANNEL_V1).target(STREAMER_ID).build());
			verify(webSocketPool).removeTopic(Topic.builder().name(RAID).target(STREAMER_ID).build());
			verify(twitchIrcClient, never()).join(any());
		}
	}
	
	@Test
	void updateStreamerNothingActivatedOffline(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			tested.start();
			
			when(streamerSettings.isMakePredictions()).thenReturn(false);
			when(streamerSettings.isFollowRaid()).thenReturn(false);
			lenient().when(streamerSettings.isJoinIrc()).thenReturn(false);
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(false);
			
			tested.getStreamerMap().put(STREAMER_ID, streamer);
			
			assertDoesNotThrow(() -> tested.updateStreamer(streamer));
			
			verify(webSocketPool).removeTopic(Topic.builder().name(PREDICTIONS_CHANNEL_V1).target(STREAMER_ID).build());
			verify(webSocketPool).removeTopic(Topic.builder().name(RAID).target(STREAMER_ID).build());
			verify(twitchIrcClient, never()).join(any());
		}
	}
	
	@Test
	void getUsername(){
		var username = "username";
		when(accountConfiguration.getUsername()).thenReturn(username);
		
		assertThat(tested.getUsername()).isEqualTo(username);
	}
	
	@Test
	void eventHandlers(){
		var listener1 = mock(IEventListener.class);
		var listener2 = mock(IEventListener.class);
		
		tested.addEventListener(listener1);
		tested.addEventListener(listener2);
		
		var event = mock(IEvent.class);
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(executorService, times(2)).submit(any(Runnable.class));
		verify(listener1).onEvent(event);
		verify(listener2).onEvent(event);
	}
}