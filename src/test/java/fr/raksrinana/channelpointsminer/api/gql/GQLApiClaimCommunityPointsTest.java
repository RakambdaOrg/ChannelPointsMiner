package fr.raksrinana.channelpointsminer.api.gql;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.claimcommunitypoints.ClaimCommunityPointsData;
import fr.raksrinana.channelpointsminer.api.gql.data.types.ClaimCommunityPointsError;
import fr.raksrinana.channelpointsminer.api.gql.data.types.ClaimCommunityPointsPayload;
import fr.raksrinana.channelpointsminer.api.gql.data.types.CommunityPointsClaim;
import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.tests.TestUtils;
import fr.raksrinana.channelpointsminer.tests.UnirestMockExtension;
import kong.unirest.MockClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static fr.raksrinana.channelpointsminer.api.gql.data.types.ClaimErrorCode.NOT_FOUND;
import static kong.unirest.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiClaimCommunityPointsTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String CHANNEL_ID = "channel-id";
	private static final String CLAIM_ID = "claim-id";
	private static final String VALID_QUERY = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"46aaeebe02c99afdf4fc97c7c0cba964124bf6b0af229395f1f6d1feed05b3d0\",\"version\":1}},\"operationName\":\"ClaimCommunityPoints\",\"variables\":{\"input\":{\"channelID\":\"%s\",\"claimID\":\"%s\"}}}";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void nominalClaimed(MockClient unirest){
		var expected = GQLResponse.<ClaimCommunityPointsData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 55,
						"operationName", "ClaimCommunityPoints",
						"requestID", "request-id"
				))
				.data(ClaimCommunityPointsData.builder()
						.claimCommunityPoints(ClaimCommunityPointsPayload.builder()
								.claim(CommunityPointsClaim.builder()
										.id("claim-id")
										.pointsEarnedBaseline(50)
										.pointsEarnedTotal(50)
										.build())
								.currentPoints(1500)
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(CHANNEL_ID, CLAIM_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/claimCommunityPoints_claimed.json"))
				.withStatus(200);
		
		assertThat(tested.claimCommunityPoints(CHANNEL_ID, CLAIM_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void nominalNotFound(MockClient unirest){
		var expected = GQLResponse.<ClaimCommunityPointsData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 7,
						"operationName", "ClaimCommunityPoints",
						"requestID", "request-id"
				))
				.data(ClaimCommunityPointsData.builder()
						.claimCommunityPoints(ClaimCommunityPointsPayload.builder()
								.error(ClaimCommunityPointsError.builder()
										.code(NOT_FOUND)
										.build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(CHANNEL_ID, CLAIM_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/claimCommunityPoints_notFound.json"))
				.withStatus(200);
		
		assertThat(tested.claimCommunityPoints(CHANNEL_ID, CLAIM_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(CHANNEL_ID, CLAIM_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.claimCommunityPoints(CHANNEL_ID, CLAIM_ID));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(CHANNEL_ID, CLAIM_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidRequest.json"))
				.withStatus(200);
		
		assertThat(tested.claimCommunityPoints(CHANNEL_ID, CLAIM_ID)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(CHANNEL_ID, CLAIM_ID))
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.claimCommunityPoints(CHANNEL_ID, CLAIM_ID)).isEmpty();
		
		unirest.verifyAll();
	}
}