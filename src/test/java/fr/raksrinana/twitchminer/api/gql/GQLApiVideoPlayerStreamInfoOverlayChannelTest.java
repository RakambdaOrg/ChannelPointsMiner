package fr.raksrinana.twitchminer.api.gql;

import fr.raksrinana.twitchminer.TestUtils;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.types.*;
import fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import kong.unirest.MockClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import static kong.unirest.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GQLApiVideoPlayerStreamInfoOverlayChannelTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String USERNAME = "username";
	public static final String VALID_QUERY = "{\"operationName\":\"VideoPlayerStreamInfoOverlayChannel\",\"extensions\":{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"a5f2e34d626a9f4f5c0204f910bab2194948a9502089be558bb6e779a9e1b3d2\"}},\"variables\":{\"channel\":\"%s\"}}";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	private MockClient unirest;
	
	@BeforeEach
	void setUp(){
		TestUtils.setupUnirest();
		unirest = MockClient.register();
		
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void nominalOnline() throws MalformedURLException{
		var expected = GQLResponse.<VideoPlayerStreamInfoOverlayChannelData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 58,
						"operationName", "VideoPlayerStreamInfoOverlayChannel",
						"requestID", "request-id"
				))
				.data(VideoPlayerStreamInfoOverlayChannelData.builder()
						.user(User.builder()
								.id("123456789")
								.profileUrl(new URL("https://google.com/streamer"))
								.displayName("streamername")
								.login("streamer")
								.profileImageUrl(new URL("https://google.com/streamer/profile"))
								.broadcastSettings(BroadcastSettings.builder()
										.id("147258369")
										.title("title")
										.game(Game.builder()
												.id("123")
												.displayName("gamename")
												.name("game")
												.build())
										.build())
								.stream(Stream.builder()
										.id("369258147")
										.viewersCount(2586)
										.tags(List.of(
												Tag.builder()
														.id("tag-id")
														.localizedName("name")
														.build()
										))
										.build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/videoPlayerStreamInfoOverlayChannel_online.json"))
				.withStatus(200);
		
		assertThat(tested.videoPlayerStreamInfoOverlayChannel(USERNAME)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void nominalOffline() throws MalformedURLException{
		var expected = GQLResponse.<VideoPlayerStreamInfoOverlayChannelData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 58,
						"operationName", "VideoPlayerStreamInfoOverlayChannel",
						"requestID", "request-id"
				))
				.data(VideoPlayerStreamInfoOverlayChannelData.builder()
						.user(User.builder()
								.id("123456789")
								.profileUrl(new URL("https://google.com/streamer"))
								.displayName("streamername")
								.login("streamer")
								.profileImageUrl(new URL("https://google.com/streamer/profile"))
								.broadcastSettings(BroadcastSettings.builder()
										.id("147258369")
										.title("title")
										.game(Game.builder()
												.id("123")
												.displayName("gamename")
												.name("game")
												.build())
										.build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/videoPlayerStreamInfoOverlayChannel_offline.json"))
				.withStatus(200);
		
		assertThat(tested.videoPlayerStreamInfoOverlayChannel(USERNAME)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.videoPlayerStreamInfoOverlayChannel(USERNAME));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidRequest.json"))
				.withStatus(200);
		
		assertThat(tested.videoPlayerStreamInfoOverlayChannel(USERNAME)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.videoPlayerStreamInfoOverlayChannel(USERNAME)).isEmpty();
		
		unirest.verifyAll();
	}
}