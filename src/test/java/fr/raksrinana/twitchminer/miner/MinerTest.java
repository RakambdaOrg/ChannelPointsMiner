package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import fr.raksrinana.twitchminer.api.kraken.KrakenApi;
import fr.raksrinana.twitchminer.api.kraken.data.follows.Channel;
import fr.raksrinana.twitchminer.api.kraken.data.follows.Follow;
import fr.raksrinana.twitchminer.api.passport.PassportApi;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import fr.raksrinana.twitchminer.api.passport.exceptions.CaptchaSolveRequired;
import fr.raksrinana.twitchminer.api.passport.exceptions.LoginException;
import fr.raksrinana.twitchminer.api.twitch.TwitchApi;
import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.twitchminer.api.ws.data.message.ClaimAvailable;
import fr.raksrinana.twitchminer.api.ws.data.message.Message;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.twitchminer.config.Configuration;
import fr.raksrinana.twitchminer.config.StreamerConfiguration;
import fr.raksrinana.twitchminer.factory.*;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import fr.raksrinana.twitchminer.miner.data.StreamerSettings;
import fr.raksrinana.twitchminer.miner.handler.MessageHandler;
import fr.raksrinana.twitchminer.miner.runnables.UpdateChannelPointsContext;
import fr.raksrinana.twitchminer.miner.runnables.UpdateStreamInfo;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import static fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinerTest{
	private static final String STREAMER_USERNAME = "streamer-username";
	private static final String STREAMER_ID = "streamer-id";
	private static final String USER_ID = "user-id";
	private static final String ACCESS_TOKEN = "access-token";
	
	private Miner tested;
	
	@Mock
	private Configuration configuration;
	@Mock
	private PassportApi passportApi;
	@Mock
	private TwitchWebSocketPool webSocketPool;
	@Mock
	private StreamerConfiguration streamerConfiguration;
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
	private UpdateChannelPointsContext updateChannelPointsContext;
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
	
	@BeforeEach
	void setUp() throws LoginException, IOException{
		tested = new Miner(configuration, passportApi, streamerSettingsFactory, webSocketPool, scheduledExecutorService, executorService);
		
		lenient().when(passportApi.login()).thenReturn(twitchLogin);
		lenient().when(streamerSettingsFactory.createStreamerSettings()).thenReturn(streamerSettings);
		lenient().when(streamerSettings.isFollowRaid()).thenReturn(false);
		lenient().when(streamerSettings.isMakePredictions()).thenReturn(false);
		lenient().when(twitchLogin.fetchUserId()).thenReturn(USER_ID);
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
		
		lenient().when(streamerConfiguration.getUsername()).thenReturn(STREAMER_USERNAME);
		
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
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class);
				var runnableFactory = Mockito.mockStatic(MinerRunnableFactory.class);
				var eventLoggerFactory = Mockito.mockStatic(EventLoggerFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateChannelPointsContext(tested)).thenReturn(updateChannelPointsContext);
			
			eventLoggerFactory.when(() -> EventLoggerFactory.create(tested)).thenReturn(eventLogger);
			
			when(configuration.getStreamers()).thenReturn(Set.of(streamerConfiguration));
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).update(expectedStreamer);
			verify(updateChannelPointsContext).update(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
		}
	}
	
	@Test
	void setupIsDoneFromConfigWithPredictions() throws LoginException, IOException{
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class);
				var runnableFactory = Mockito.mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateChannelPointsContext(tested)).thenReturn(updateChannelPointsContext);
			
			when(streamerSettings.isMakePredictions()).thenReturn(true);
			when(configuration.getStreamers()).thenReturn(Set.of(streamerConfiguration));
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).update(expectedStreamer);
			verify(updateChannelPointsContext).update(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_CHANNEL_V1, STREAMER_ID, ACCESS_TOKEN));
		}
	}
	
	@Test
	void setupIsDoneFromConfigWithRaid() throws LoginException, IOException{
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class);
				var runnableFactory = Mockito.mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateChannelPointsContext(tested)).thenReturn(updateChannelPointsContext);
			
			when(streamerSettings.isFollowRaid()).thenReturn(true);
			when(configuration.getStreamers()).thenReturn(Set.of(streamerConfiguration));
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).update(expectedStreamer);
			verify(updateChannelPointsContext).update(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(RAID, STREAMER_ID, ACCESS_TOKEN));
		}
	}
	
	@Test
	void setupIsDoneFromConfigWithUnknownUser() throws LoginException, IOException{
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class);
				var runnableFactory = Mockito.mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateChannelPointsContext(tested)).thenReturn(updateChannelPointsContext);
			
			when(configuration.getStreamers()).thenReturn(Set.of(streamerConfiguration));
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.empty());
			
			assertDoesNotThrow(() -> tested.start());
			
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).isEmpty();
			
			verify(passportApi).login();
			verify(updateStreamInfo, never()).update(any());
			verify(updateChannelPointsContext, never()).update(any());
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool, never()).listenTopic(Topics.buildFromName(PREDICTIONS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool, never()).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool, never()).listenTopic(Topics.buildFromName(PREDICTIONS_CHANNEL_V1, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool, never()).listenTopic(Topics.buildFromName(RAID, STREAMER_ID, ACCESS_TOKEN));
		}
	}
	
	@Test
	void setupIsDoneFromFollows() throws LoginException, IOException{
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class);
				var runnableFactory = Mockito.mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			apiFactory.when(() -> ApiFactory.createKrakenApi(twitchLogin)).thenReturn(krakenApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateChannelPointsContext(tested)).thenReturn(updateChannelPointsContext);
			
			var channel = mock(Channel.class);
			var follow = mock(Follow.class);
			when(follow.getChannel()).thenReturn(channel);
			when(channel.getId()).thenReturn(STREAMER_ID);
			when(channel.getName()).thenReturn(STREAMER_USERNAME);
			
			when(configuration.isLoadFollows()).thenReturn(true);
			when(configuration.getStreamers()).thenReturn(Set.of());
			when(krakenApi.getFollows()).thenReturn(List.of(follow));
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).update(expectedStreamer);
			verify(updateChannelPointsContext).update(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
		}
	}
	
	@Test
	void setupIsDoneFromFollowsWithPredictions() throws LoginException, IOException{
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class);
				var runnableFactory = Mockito.mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			apiFactory.when(() -> ApiFactory.createKrakenApi(twitchLogin)).thenReturn(krakenApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateChannelPointsContext(tested)).thenReturn(updateChannelPointsContext);
			
			var channel = mock(Channel.class);
			var follow = mock(Follow.class);
			when(follow.getChannel()).thenReturn(channel);
			when(channel.getId()).thenReturn(STREAMER_ID);
			when(channel.getName()).thenReturn(STREAMER_USERNAME);
			
			when(configuration.isLoadFollows()).thenReturn(true);
			when(streamerSettings.isMakePredictions()).thenReturn(true);
			when(configuration.getStreamers()).thenReturn(Set.of(streamerConfiguration));
			when(krakenApi.getFollows()).thenReturn(List.of(follow));
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).update(expectedStreamer);
			verify(updateChannelPointsContext).update(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(PREDICTIONS_CHANNEL_V1, STREAMER_ID, ACCESS_TOKEN));
		}
	}
	
	@Test
	void setupIsDoneFromFollowsWithRaid() throws LoginException, IOException{
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class);
				var runnableFactory = Mockito.mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			apiFactory.when(() -> ApiFactory.createKrakenApi(twitchLogin)).thenReturn(krakenApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateChannelPointsContext(tested)).thenReturn(updateChannelPointsContext);
			
			var channel = mock(Channel.class);
			var follow = mock(Follow.class);
			when(follow.getChannel()).thenReturn(channel);
			when(channel.getId()).thenReturn(STREAMER_ID);
			when(channel.getName()).thenReturn(STREAMER_USERNAME);
			
			when(configuration.isLoadFollows()).thenReturn(true);
			when(streamerSettings.isFollowRaid()).thenReturn(true);
			when(configuration.getStreamers()).thenReturn(Set.of(streamerConfiguration));
			when(krakenApi.getFollows()).thenReturn(List.of(follow));
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).update(expectedStreamer);
			verify(updateChannelPointsContext).update(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(RAID, STREAMER_ID, ACCESS_TOKEN));
		}
	}
	
	@Test
	void duplicateStreamerIsAddedOnce() throws LoginException, IOException{
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class);
				var runnableFactory = Mockito.mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			apiFactory.when(() -> ApiFactory.createKrakenApi(twitchLogin)).thenReturn(krakenApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateChannelPointsContext(tested)).thenReturn(updateChannelPointsContext);
			
			var channel = mock(Channel.class);
			var follow = mock(Follow.class);
			when(follow.getChannel()).thenReturn(channel);
			when(channel.getName()).thenReturn(STREAMER_USERNAME);
			
			when(configuration.isLoadFollows()).thenReturn(true);
			when(configuration.getStreamers()).thenReturn(Set.of(streamerConfiguration));
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
			when(krakenApi.getFollows()).thenReturn(List.of(follow));
			
			assertDoesNotThrow(() -> tested.start());
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getTwitchApi()).isEqualTo(twitchApi);
			assertThat(tested.getGqlApi()).isEqualTo(gqlApi);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(passportApi).login();
			verify(updateStreamInfo).update(expectedStreamer);
			verify(updateChannelPointsContext).update(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
		}
	}
	
	@Test
	void addDuplicateStreamer(){
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class);
				var runnableFactory = Mockito.mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(ApiFactory::createTwitchApi).thenReturn(twitchApi);
			apiFactory.when(() -> ApiFactory.createGqlApi(twitchLogin)).thenReturn(gqlApi);
			
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateStreamInfo(tested)).thenReturn(updateStreamInfo);
			runnableFactory.when(() -> MinerRunnableFactory.createUpdateChannelPointsContext(tested)).thenReturn(updateChannelPointsContext);
			
			when(configuration.getStreamers()).thenReturn(Set.of(streamerConfiguration));
			when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
			
			tested.start();
			
			var duplicateStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME.toUpperCase(), streamerSettings);
			
			assertDoesNotThrow(() -> tested.addStreamer(duplicateStreamer));
			
			var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
			assertThat(tested.getStreamers()).hasSize(1)
					.first().usingRecursiveComparison().isEqualTo(expectedStreamer);
			
			verify(updateStreamInfo).update(expectedStreamer);
			verify(updateChannelPointsContext).update(expectedStreamer);
			verify(webSocketPool).listenTopic(Topics.buildFromName(COMMUNITY_POINTS_USER_V1, USER_ID, ACCESS_TOKEN));
			verify(webSocketPool).listenTopic(Topics.buildFromName(VIDEO_PLAYBACK_BY_ID, STREAMER_ID, ACCESS_TOKEN));
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
		assertDoesNotThrow(() -> tested.close());
		
		verify(scheduledExecutorService).shutdown();
		verify(executorService).shutdown();
		verify(webSocketPool).close();
	}
	
	@Test
	void unknownMessageIsNotForwarded(){
		var message = mock(Message.class);
		assertDoesNotThrow(() -> tested.onTwitchMessage(topic, message));
		
		verify(executorService).submit(any(Runnable.class));
	}
	
	@Test
	void claimAvailableIsForwardedAndHandlerCreatedOnce(){
		try(var factory = mockStatic(MessageHandlerFactory.class)){
			var handler = (MessageHandler<ClaimAvailable>) mock(MessageHandler.class);
			factory.when(() -> MessageHandlerFactory.createClaimAvailableHandler(tested)).thenReturn(handler);
			
			var message = mock(ClaimAvailable.class);
			assertDoesNotThrow(() -> tested.onTwitchMessage(topic, message));
			assertDoesNotThrow(() -> tested.onTwitchMessage(topic, message));
			
			verify(executorService, times(2)).submit(any(Runnable.class));
			verify(handler, times(2)).handle(topic, message);
			factory.verify(() -> MessageHandlerFactory.createClaimAvailableHandler(tested), times(1));
		}
	}
}