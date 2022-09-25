package fr.raksrinana.channelpointsminer.miner.miner;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.version.IVersionProvider;
import fr.raksrinana.channelpointsminer.miner.api.passport.PassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.CaptchaSolveRequired;
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.raksrinana.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.miner.api.ws.TwitchPubSubWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.IPubSubMessage;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topics;
import fr.raksrinana.channelpointsminer.miner.config.AccountConfiguration;
import fr.raksrinana.channelpointsminer.miner.config.AnalyticsConfiguration;
import fr.raksrinana.channelpointsminer.miner.config.ChatMode;
import fr.raksrinana.channelpointsminer.miner.config.VersionProvider;
import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.event.IEvent;
import fr.raksrinana.channelpointsminer.miner.event.IEventHandler;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamerAddedEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamerRemovedEvent;
import fr.raksrinana.channelpointsminer.miner.factory.ApiFactory;
import fr.raksrinana.channelpointsminer.miner.factory.MinerRunnableFactory;
import fr.raksrinana.channelpointsminer.miner.factory.StreamerSettingsFactory;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.factory.TwitchChatFactory;
import fr.raksrinana.channelpointsminer.miner.handler.IPubSubMessageHandler;
import fr.raksrinana.channelpointsminer.miner.runnable.StreamerConfigurationReload;
import fr.raksrinana.channelpointsminer.miner.runnable.SyncInventory;
import fr.raksrinana.channelpointsminer.miner.runnable.UpdateStreamInfo;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import fr.raksrinana.channelpointsminer.miner.streamer.StreamerSettings;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.assertj.core.api.Assertions;
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
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.COMMUNITY_MOMENTS_CHANNEL_V1;
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.COMMUNITY_POINTS_USER_V1;
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.PREDICTIONS_CHANNEL_V1;
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.PREDICTIONS_USER_V1;
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.RAID;
import static fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.TopicName.VIDEO_PLAYBACK_BY_ID;
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

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class MinerTest{
	private static final String STREAMER_USERNAME = "streamer-username";
	private static final String STREAMER_ID = "streamer-id";
	private static final String USER_ID = "user-id";
	private static final String ACCESS_TOKEN = "access-token";
	private static final ChatMode CHAT_MODE = ChatMode.WS;
	private static final VersionProvider VERSION_PROVIDER = VersionProvider.WEBPAGE;
	private static final Instant NOW = Instant.parse("2020-05-17T12:14:20.000Z");
	
	private Miner tested;
	
	@Mock
	private AccountConfiguration accountConfiguration;
	@Mock
	private AnalyticsConfiguration analyticsConfiguration;
	@Mock
	private PassportApi passportApi;
	@Mock
	private TwitchPubSubWebSocketPool webSocketPool;
	@Mock
	private StreamerSettingsFactory streamerSettingsFactory;
	@Mock
	private ScheduledExecutorService scheduledExecutorService;
	@Mock
	private ExecutorService executorService;
	@Mock
	private IDatabase database;
	
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private IIntegrityProvider integrityProvider;
	@Mock
	private IVersionProvider versionProvider;
	@Mock
	private StreamerSettings streamerSettings;
	@Mock
	private TwitchApi twitchApi;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private UpdateStreamInfo updateStreamInfo;
	@Mock
	private SyncInventory syncInventory;
	@Mock
	private Topic topic;
	@Mock
	private ITwitchChatClient twitchChatClient;
	@Mock
	private StreamerConfigurationReload streamerConfigurationReload;
	@Mock
	private IEventHandler eventHandler;
	
	@BeforeEach
	void setUp() throws LoginException, IOException{
		tested = new Miner(accountConfiguration, passportApi, streamerSettingsFactory, webSocketPool, scheduledExecutorService, executorService, database);
		
		lenient().when(accountConfiguration.getReloadEvery()).thenReturn(0);
		lenient().when(accountConfiguration.isLoadFollows()).thenReturn(false);
		lenient().when(accountConfiguration.getChatMode()).thenReturn(CHAT_MODE);
		lenient().when(accountConfiguration.getAnalytics()).thenReturn(analyticsConfiguration);
		lenient().when(accountConfiguration.getVersionProvider()).thenReturn(VERSION_PROVIDER);
		lenient().when(analyticsConfiguration.isEnabled()).thenReturn(false);
		lenient().when(analyticsConfiguration.isRecordChatsPredictions()).thenReturn(false);
		
		lenient().when(passportApi.login()).thenReturn(twitchLogin);
		lenient().when(streamerSettings.isFollowRaid()).thenReturn(false);
		lenient().when(streamerSettings.isMakePredictions()).thenReturn(false);
		lenient().when(streamerSettings.isJoinIrc()).thenReturn(false);
		lenient().when(twitchLogin.fetchUserId(gqlApi)).thenReturn(USER_ID);
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
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createStreamerConfigurationReload(tested, streamerSettingsFactory, false)).thenReturn(streamerConfigurationReload);
			
			assertDoesNotThrow(() -> tested.start());
			
			Assertions.assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			Assertions.assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			Assertions.assertThat(tested.getChatClient()).isEqualTo(twitchChatClient);
			Assertions.assertThat(tested.getStreamers()).isEmpty();
			
			verify(passportApi).login();
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(twitchChatClient, never()).join(any());
			verify(scheduledExecutorService).schedule(eq(streamerConfigurationReload), anyLong(), any());
			verify(twitchChatClient).addChatMessageListener(any());
		}
	}
	
	@Test
	void setupIsDoneWithConfigReload() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createStreamerConfigurationReload(tested, streamerSettingsFactory, true)).thenReturn(streamerConfigurationReload);
			
			lenient().when(accountConfiguration.getReloadEvery()).thenReturn(15);
			lenient().when(accountConfiguration.isLoadFollows()).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.start());
			
			Assertions.assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			Assertions.assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			Assertions.assertThat(tested.getStreamers()).isEmpty();
			
			verify(passportApi).login();
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(scheduledExecutorService).scheduleWithFixedDelay(eq(streamerConfigurationReload), anyLong(), eq(15L), eq(MINUTES));
		}
	}
	
	@Test
	void setupIsDoneWithConfigReloadAndFollows() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createStreamerConfigurationReload(tested, streamerSettingsFactory, false)).thenReturn(streamerConfigurationReload);
			
			lenient().when(accountConfiguration.getReloadEvery()).thenReturn(15);
			
			assertDoesNotThrow(() -> tested.start());
			
			Assertions.assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			Assertions.assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			Assertions.assertThat(tested.getStreamers()).isEmpty();
			
			verify(passportApi).login();
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(scheduledExecutorService).scheduleWithFixedDelay(eq(streamerConfigurationReload), anyLong(), eq(15L), eq(MINUTES));
		}
	}
	
	@Test
	void setupIsDoneWithAnalytics() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createStreamerConfigurationReload(tested, streamerSettingsFactory, false)).thenReturn(streamerConfigurationReload);
			
			when(analyticsConfiguration.isEnabled()).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.start());
			
			Assertions.assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			Assertions.assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			Assertions.assertThat(tested.getChatClient()).isEqualTo(twitchChatClient);
			Assertions.assertThat(tested.getStreamers()).isEmpty();
			
			verify(passportApi).login();
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(twitchChatClient, never()).join(any());
			verify(scheduledExecutorService).schedule(eq(streamerConfigurationReload), anyLong(), any());
			verify(twitchChatClient).addChatMessageListener(any());
		}
	}
	
	@Test
	void setupIsDoneWithAnalyticsAndPredictionRecording() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, true)).thenReturn(twitchChatClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createStreamerConfigurationReload(tested, streamerSettingsFactory, false)).thenReturn(streamerConfigurationReload);
			
			when(analyticsConfiguration.isEnabled()).thenReturn(true);
			when(analyticsConfiguration.isRecordChatsPredictions()).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.start());
			
			Assertions.assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			Assertions.assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			Assertions.assertThat(tested.getChatClient()).isEqualTo(twitchChatClient);
			Assertions.assertThat(tested.getStreamers()).isEmpty();
			
			verify(passportApi).login();
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(twitchChatClient, never()).join(any());
			verify(scheduledExecutorService).schedule(eq(streamerConfigurationReload), anyLong(), any());
			verify(twitchChatClient).addChatMessageListener(any());
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
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
			tested.addEventHandler(eventHandler);
			tested.start();
			
			assertDoesNotThrow(() -> tested.close());
			
			verify(scheduledExecutorService).shutdown();
			verify(executorService).shutdown();
			verify(webSocketPool).close();
			verify(twitchChatClient).close();
			verify(eventHandler).close();
		}
	}
	
	@Test
	void unknownMessageIsNotForwarded(){
		var message = mock(IPubSubMessage.class);
		assertDoesNotThrow(() -> tested.onTwitchMessage(topic, message));
		
		verify(executorService, never()).submit(any(Runnable.class));
	}
	
	@Test
	void messageHandlersAreCalled(){
		var handler1 = mock(IPubSubMessageHandler.class);
		var handler2 = mock(IPubSubMessageHandler.class);
		
		tested.addPubSubHandler(handler1);
		tested.addPubSubHandler(handler2);
		
		var message = mock(IPubSubMessage.class);
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
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamerSettings.isMakePredictions()).thenReturn(true);
			
			tested.addEventHandler(eventHandler);
			tested.start();
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			
			Assertions.assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(streamer);
			
			verify(updateStreamInfo).run(streamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_CHANNEL_V1, STREAMER_ID, ACCESS_TOKEN));
			verify(eventHandler).onEvent(new StreamerAddedEvent(tested, streamer, NOW));
		}
	}
	
	@Test
	void addStreamerWithMoments(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamerSettings.isClaimMoments()).thenReturn(true);
			
			tested.addEventHandler(eventHandler);
			tested.start();
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			
			Assertions.assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(streamer);
			
			verify(updateStreamInfo).run(streamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_MOMENTS_CHANNEL_V1, STREAMER_ID, ACCESS_TOKEN));
			verify(eventHandler).onEvent(new StreamerAddedEvent(tested, streamer, NOW));
		}
	}
	
	@Test
	void addStreamerWithRaid(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamerSettings.isFollowRaid()).thenReturn(true);
			
			tested.addEventHandler(eventHandler);
			tested.start();
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			
			Assertions.assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(streamer);
			
			verify(updateStreamInfo).run(streamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(RAID, STREAMER_ID, ACCESS_TOKEN));
			verify(eventHandler).onEvent(new StreamerAddedEvent(tested, streamer, NOW));
		}
	}
	
	@Test
	void addStreamerWithIrcAndStreamerOffline(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class);
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			lenient().when(streamerSettings.isJoinIrc()).thenReturn(true);
			
			tested.addEventHandler(eventHandler);
			tested.start();
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			
			Assertions.assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(streamer);
			
			verify(updateStreamInfo).run(streamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(eventHandler).onEvent(new StreamerAddedEvent(tested, streamer, NOW));
			verify(twitchChatClient, never()).join(any());
		}
	}
	
	@Test
	void addStreamerWithIrcAndStreamerOnline(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class);
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			lenient().when(streamerSettings.isJoinIrc()).thenReturn(true);
			
			tested.addEventHandler(eventHandler);
			tested.start();
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			
			Assertions.assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(streamer);
			
			verify(updateStreamInfo).run(streamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(eventHandler).onEvent(new StreamerAddedEvent(tested, streamer, NOW));
			verify(twitchChatClient).join(STREAMER_USERNAME);
		}
	}
	
	@Test
	void addDuplicateStreamer(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			tested.addEventHandler(eventHandler);
			tested.start();
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			when(streamer.getSettings()).thenReturn(streamerSettings);
			when(streamer.isStreaming()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			assertDoesNotThrow(() -> tested.addStreamer(streamer));
			
			Assertions.assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(streamer);
			
			verify(updateStreamInfo).run(streamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(eventHandler).onEvent(new StreamerAddedEvent(tested, streamer, NOW));
		}
	}
	
	@Test
	void getStreamerByIdUnknown(){
		Assertions.assertThat(tested.getStreamerById("unknown")).isEmpty();
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
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
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
			
			Assertions.assertThat(tested.getStreamerById(id1)).isPresent()
					.get().isEqualTo(streamer1);
			Assertions.assertThat(tested.getStreamerById(id2)).isPresent()
					.get().isEqualTo(streamer2);
		}
	}
	
	@Test
	void removeStreamer(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var timeFactory = mockStatic(TimeFactory.class);
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			tested.getStreamerMap().put(STREAMER_ID, streamer);
			tested.addEventHandler(eventHandler);
			
			tested.start();
			tested.removeStreamer(streamer);
			
			verify(webSocketPool).removeTopic(Topic.builder().name(VIDEO_PLAYBACK_BY_ID).target(STREAMER_ID).build());
			verify(webSocketPool).removeTopic(Topic.builder().name(PREDICTIONS_CHANNEL_V1).target(STREAMER_ID).build());
			verify(webSocketPool).removeTopic(Topic.builder().name(COMMUNITY_MOMENTS_CHANNEL_V1).target(STREAMER_ID).build());
			verify(webSocketPool).removeTopic(Topic.builder().name(RAID).target(STREAMER_ID).build());
			verify(twitchChatClient).leave(STREAMER_USERNAME);
			verify(eventHandler).onEvent(new StreamerRemovedEvent(tested, streamer, NOW));
		}
	}
	
	@Test
	void removeUnknownStreamer(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			tested.addEventHandler(eventHandler);
			
			var streamer = mock(Streamer.class);
			when(streamer.getId()).thenReturn(STREAMER_ID);
			when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
			
			tested.start();
			tested.removeStreamer(streamer);
			
			verify(webSocketPool, never()).removeTopic(any());
			verify(twitchChatClient, never()).leave(any());
			verify(eventHandler, never()).onEvent(any());
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
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
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
			verify(twitchChatClient).join(STREAMER_USERNAME);
		}
	}
	
	@Test
	void updateStreamerAllActivatedOffline(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
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
			verify(twitchChatClient, never()).join(any());
		}
	}
	
	@Test
	void updateStreamerNothingActivatedOnline(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
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
			verify(twitchChatClient, never()).join(any());
		}
	}
	
	@Test
	void updateStreamerNothingActivatedOffline(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
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
			verify(twitchChatClient, never()).join(any());
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
		var handler1 = mock(IEventHandler.class);
		var handler2 = mock(IEventHandler.class);
		
		tested.addEventHandler(handler1);
		tested.addEventHandler(handler2);
		
		var event = mock(IEvent.class);
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(executorService, times(2)).submit(any(Runnable.class));
		verify(handler1).onEvent(event);
		verify(handler2).onEvent(event);
	}
	
	@Test
	void requestInventorySync(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchChatFactory.class)){
			apiFactory.when(() -> ApiFactory.createTwitchApi(twitchLogin)).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createVersionProvider(VERSION_PROVIDER)).thenReturn(versionProvider);
			apiFactory.when(() -> ApiFactory.createIntegrityProvider(twitchLogin, versionProvider)).thenReturn(integrityProvider);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin, integrityProvider)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchChatFactory.createChat(tested, CHAT_MODE, false)).thenReturn(twitchChatClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createSyncInventory(tested)).thenReturn(syncInventory);
			
			assertDoesNotThrow(() -> tested.syncInventory());
			
			verify(syncInventory).run();
		}
	}
}