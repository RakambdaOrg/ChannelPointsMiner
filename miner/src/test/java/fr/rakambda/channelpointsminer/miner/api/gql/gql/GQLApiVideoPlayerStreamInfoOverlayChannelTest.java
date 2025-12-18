package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.BroadcastSettings;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Game;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Stream;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
								.login("streamer")
								.profileImageUrl(URI.create("https://google.com/streamer/profile").toURL())
								.broadcastSettings(BroadcastSettings.builder()
										.game(Game.builder()
												.id("123")
												.name("game")
												.build())
										.build())
								.stream(Stream.builder()
										.id("369258147")
										.build())
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/videoPlayerStreamInfoOverlayChannel_online.json");
		
		assertThat(tested.videoPlayerStreamInfoOverlayChannel(USERNAME)).contains(expected);
		
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
								.login("streamer")
								.profileImageUrl(URI.create("https://google.com/streamer/profile").toURL())
								.broadcastSettings(BroadcastSettings.builder()
										.game(Game.builder()
												.id("123")
												.name("game")
												.build())
										.build())
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/videoPlayerStreamInfoOverlayChannel_offline.json");
		
		assertThat(tested.videoPlayerStreamInfoOverlayChannel(USERNAME)).contains(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"e785b65ff71ad7b363b34878335f27dd9372869ad0c5740a130b9268bcdbe7e7\",\"version\":1}},\"operationName\":\"VideoPlayerStreamInfoOverlayChannel\",\"variables\":{\"channel\":\"%s\"}}".formatted(USERNAME);
	}
}