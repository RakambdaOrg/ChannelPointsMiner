package fr.raksrinana.channelpointsminer.miner;

import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.channelpointsminer.api.gql.data.types.User;
import fr.raksrinana.channelpointsminer.api.kraken.KrakenApi;
import fr.raksrinana.channelpointsminer.api.kraken.data.follows.Channel;
import fr.raksrinana.channelpointsminer.api.kraken.data.follows.Follow;
import fr.raksrinana.channelpointsminer.api.passport.PassportApi;
import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.api.passport.exceptions.CaptchaSolveRequired;
import fr.raksrinana.channelpointsminer.api.passport.exceptions.LoginException;
import fr.raksrinana.channelpointsminer.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.channelpointsminer.api.ws.data.message.Message;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.channelpointsminer.config.Configuration;
import fr.raksrinana.channelpointsminer.factory.ApiFactory;
import fr.raksrinana.channelpointsminer.factory.MinerRunnableFactory;
import fr.raksrinana.channelpointsminer.factory.StreamerSettingsFactory;
import fr.raksrinana.channelpointsminer.handler.EventLogger;
import fr.raksrinana.channelpointsminer.handler.MessageHandler;
import fr.raksrinana.channelpointsminer.irc.TwitchIrcClient;
import fr.raksrinana.channelpointsminer.irc.TwitchIrcFactory;
import fr.raksrinana.channelpointsminer.runnable.UpdateStreamInfo;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import fr.raksrinana.channelpointsminer.streamer.StreamerSettings;
import lombok.SneakyThrows;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import static fr.raksrinana.channelpointsminer.api.ws.data.request.topic.TopicName.*;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinerTest{
	private static final String STREAMER_USERNAME = "streamer-username";
	private static final String STREAMER_ID = "streamer-id";
	private static final String USER_ID = "user-id";
	private static final String ACCESS_TOKEN = "access-token";
	
	private Miner tested;
	
	@TempDir
	private Path tempDir;
	
	@Mock
	private Configuration configuration;
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
	private KrakenApi krakenApi;
	@Mock
	private UpdateStreamInfo updateStreamInfo;
	@Mock
	private User user;
	@Mock
	private ReportMenuItemData reportMenuItemData;
	@Mock
	private GQLResponse<ReportMenuItemData> reportMenuItemResponse;
	@Mock
	private Topic topic;
	@Mock
	private EventLogger eventLogger;
	@Mock
	private TwitchIrcClient twitchIrcClient;
	
	@BeforeEach
	void setUp() throws LoginException, IOException{
		tested = new Miner(configuration, passportApi, streamerSettingsFactory, webSocketPool, scheduledExecutorService, executorService);
		
		lenient().when(configuration.getStreamerConfigDirectory()).thenReturn(tempDir);
		
		lenient().when(passportApi.login()).thenReturn(twitchLogin);
		lenient().when(streamerSettingsFactory.createStreamerSettings(STREAMER_USERNAME)).thenReturn(streamerSettings);
		lenient().when(streamerSettings.isFollowRaid()).thenReturn(false);
		lenient().when(streamerSettings.isMakePredictions()).thenReturn(false);
		lenient().when(streamerSettings.isJoinIrc()).thenReturn(false);
		lenient().when(twitchLogin.fetchUserId()).thenReturn(USER_ID);
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
		
		lenient().when(reportMenuItemResponse.getData()).thenReturn(reportMenuItemData);
		lenient().when(reportMenuItemData.getUser()).thenReturn(user);
		lenient().when(user.getId()).thenReturn(STREAMER_ID);
		
		lenient().when(executorService.submit(any(Runnable.class))).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return CompletableFuture.completedFuture(null);
		});
	}
	
	@Test
	void setupIsDoneFromConfig() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
			
			setupStreamerConfig(STREAMER_USERNAME);
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).run(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient, never()).join(any());
		}
	}
	
	@SneakyThrows
	private void setupStreamerConfig(String username){
		Files.writeString(tempDir.resolve(username + ".json"), "{}", TRUNCATE_EXISTING, CREATE);
	}
	
	@Test
	void setupIsDoneFromConfigWithJoinIrc() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			when(streamerSettings.isJoinIrc()).thenReturn(true);
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
			
			setupStreamerConfig(STREAMER_USERNAME);
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).run(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient).join(STREAMER_USERNAME);
		}
	}
	
	@Test
	void setupIsDoneFromConfigWithPredictions() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			when(streamerSettings.isMakePredictions()).thenReturn(true);
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
			
			setupStreamerConfig(STREAMER_USERNAME);
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).run(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_CHANNEL_V1, STREAMER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient, never()).join(any());
		}
	}
	
	@Test
	void setupIsDoneFromConfigWithRaid() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			when(streamerSettings.isFollowRaid()).thenReturn(true);
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
			
			setupStreamerConfig(STREAMER_USERNAME);
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).run(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(RAID, STREAMER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient, never()).join(any());
		}
	}
	
	@Test
	void setupIsDoneFromFollows() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			apiFactory.when(() -> ApiFactory.createKrakenApi(twitchLogin)).thenReturn(krakenApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			var channel = mock(Channel.class);
			var follow = mock(Follow.class);
			when(follow.getChannel()).thenReturn(channel);
			when(channel.getId()).thenReturn(STREAMER_ID);
			when(channel.getName()).thenReturn(STREAMER_USERNAME);
			
			when(configuration.isLoadFollows()).thenReturn(true);
			when(krakenApi.getFollows()).thenReturn(List.of(follow));
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).run(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient, never()).join(any());
		}
	}
	
	@Test
	void setupIsDoneFromConfigWithUnknownUser() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.empty());
			
			setupStreamerConfig(STREAMER_USERNAME);
			
			assertDoesNotThrow(() -> tested.start());
			
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).isEmpty();
			
			verify(passportApi).login();
			verify(updateStreamInfo, never()).run(any());
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool, never()).listenTopic(Topics.buildFromName(PREDICTIONS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool, never()).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool, never()).listenTopic(Topics.buildFromName(PREDICTIONS_CHANNEL_V1, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool, never()).listenTopic(Topics.buildFromName(RAID, STREAMER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient, never()).join(any());
		}
	}
	
	@Test
	void setupIsDoneFromFollowsWithPredictions() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			apiFactory.when(() -> ApiFactory.createKrakenApi(twitchLogin)).thenReturn(krakenApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			var channel = mock(Channel.class);
			var follow = mock(Follow.class);
			when(follow.getChannel()).thenReturn(channel);
			when(channel.getId()).thenReturn(STREAMER_ID);
			when(channel.getName()).thenReturn(STREAMER_USERNAME);
			
			when(configuration.isLoadFollows()).thenReturn(true);
			when(streamerSettings.isMakePredictions()).thenReturn(true);
			when(krakenApi.getFollows()).thenReturn(List.of(follow));
			
			setupStreamerConfig(STREAMER_USERNAME);
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).run(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_CHANNEL_V1, STREAMER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient, never()).join(any());
		}
	}
	
	@Test
	void setupIsDoneFromFollowsWithRaid() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			apiFactory.when(() -> ApiFactory.createKrakenApi(twitchLogin)).thenReturn(krakenApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			var channel = mock(Channel.class);
			var follow = mock(Follow.class);
			when(follow.getChannel()).thenReturn(channel);
			when(channel.getId()).thenReturn(STREAMER_ID);
			when(channel.getName()).thenReturn(STREAMER_USERNAME);
			
			when(configuration.isLoadFollows()).thenReturn(true);
			when(streamerSettings.isFollowRaid()).thenReturn(true);
			when(krakenApi.getFollows()).thenReturn(List.of(follow));
			
			setupStreamerConfig(STREAMER_USERNAME);
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).run(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(RAID, STREAMER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient, never()).join(any());
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
	void close(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			apiFactory.when(() -> ApiFactory.createKrakenApi(twitchLogin)).thenReturn(krakenApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			tested.start();
			
			assertDoesNotThrow(() -> tested.close());
			
			verify(scheduledExecutorService).shutdown();
			verify(executorService).shutdown();
			verify(webSocketPool).close();
			verify(twitchIrcClient).close();
		}
	}
	
	@Test
	void unknownMessageIsNotForwarded(){
		var message = mock(Message.class);
		assertDoesNotThrow(() -> tested.onTwitchMessage(topic, message));
		
		verify(executorService).submit(any(Runnable.class));
	}
	
	@Test
	void messageHandlersAreCalled(){
		var handler1 = mock(MessageHandler.class);
		var handler2 = mock(MessageHandler.class);
		
		tested.addHandler(handler1);
		tested.addHandler(handler2);
		
		var message = mock(Message.class);
		assertDoesNotThrow(() -> tested.onTwitchMessage(topic, message));
		
		verify(executorService).submit(any(Runnable.class));
		verify(handler1).handle(topic, message);
		verify(handler2).handle(topic, message);
	}
	
	@Test
	void duplicateStreamerIsAddedOnce() throws LoginException, IOException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			apiFactory.when(() -> ApiFactory.createKrakenApi(twitchLogin)).thenReturn(krakenApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			var channel = mock(Channel.class);
			var follow = mock(Follow.class);
			when(follow.getChannel()).thenReturn(channel);
			when(channel.getName()).thenReturn(STREAMER_USERNAME);
			
			when(configuration.isLoadFollows()).thenReturn(true);
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
			when(krakenApi.getFollows()).thenReturn(List.of(follow));
			
			setupStreamerConfig(STREAMER_USERNAME);
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).run(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(twitchIrcClient, never()).join(any());
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
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			apiFactory.when(() -> ApiFactory.createKrakenApi(twitchLogin)).thenReturn(krakenApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
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
	void addDuplicateStreamer(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var runnableFactory = mockStatic(MinerRunnableFactory.class);
				var ircFactory = mockStatic(TwitchIrcFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			ircFactory.when(() -> TwitchIrcFactory.create(twitchLogin)).thenReturn(twitchIrcClient);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
			
			setupStreamerConfig(STREAMER_USERNAME);
			
			tested.start();
			
			var duplicateStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME.toUpperCase(), streamerSettings);
			
			assertDoesNotThrow(() -> tested.addStreamer(duplicateStreamer));
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(updateStreamInfo).run(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
		}
	}
}