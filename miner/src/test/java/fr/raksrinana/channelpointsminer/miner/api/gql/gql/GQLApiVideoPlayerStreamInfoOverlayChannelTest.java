package fr.raksrinana.channelpointsminer.miner.api.gql.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types.BroadcastSettings;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types.Game;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types.Stream;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types.Tag;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMock;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiVideoPlayerStreamInfoOverlayChannelTest extends AbstractGQLTest{
	private static final String USERNAME = "username";
	
	@Test
	void nominalOnline(UnirestMock unirest) throws MalformedURLException{
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
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/videoPlayerStreamInfoOverlayChannel_online.json");
		
		assertThat(tested.videoPlayerStreamInfoOverlayChannel(USERNAME)).isPresent().get().isEqualTo(expected);
		
		verifyAll();
	}
	
	@Test
	void nominalOffline(UnirestMock unirest) throws MalformedURLException{
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
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/videoPlayerStreamInfoOverlayChannel_offline.json");
		
		assertThat(tested.videoPlayerStreamInfoOverlayChannel(USERNAME)).isPresent().get().isEqualTo(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"a5f2e34d626a9f4f5c0204f910bab2194948a9502089be558bb6e779a9e1b3d2\",\"version\":1}},\"operationName\":\"VideoPlayerStreamInfoOverlayChannel\",\"variables\":{\"channel\":\"%s\"}}".formatted(USERNAME);
	}
}