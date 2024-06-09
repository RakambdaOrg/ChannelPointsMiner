package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.playbackaccesstoken.PlaybackAccessTokenData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.StreamPlaybackAccessToken;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiPlaybackAccessTokenTest extends AbstractGQLTest{
	private static final String LOGIN = "channel-name";
	
	@Test
	void nominal(){
		var expected = GQLResponse.<PlaybackAccessTokenData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 18,
						"operationName", "PlaybackAccessToken",
						"requestID", "request-id"
				))
				.data(PlaybackAccessTokenData.builder()
						.streamPlaybackAccessToken(StreamPlaybackAccessToken.builder()
								.value("value")
								.signature("signature")
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/playbackaccesstoken_success.json");
		
		assertThat(tested.playbackAccessToken(LOGIN)).contains(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"3093517e37e4f4cb48906155bcd894150aef92617939236d2508f3375ab732ce\",\"version\":1}},\"operationName\":\"PlaybackAccessToken\",\"variables\":{\"isLive\":true,\"vodID\":\"\",\"playerType\":\"picture-by-picture\",\"isVod\":false,\"login\":\"%s\"}}".formatted(LOGIN);
	}
}