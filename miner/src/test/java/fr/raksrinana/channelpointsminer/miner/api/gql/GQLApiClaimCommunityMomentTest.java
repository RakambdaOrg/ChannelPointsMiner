package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.communitymomentcalloutclaim.CommunityMomentCalloutClaimData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.ClaimCommunityMomentPayload;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.CommunityMoment;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.TestUtils;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import kong.unirest.core.MockClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static kong.unirest.core.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiClaimCommunityMomentTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String MOMENT_ID = "moment-id";
	private static final String VALID_QUERY = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"e2d67415aead910f7f9ceb45a77b750a1e1d9622c936d832328a0689e054db62\",\"version\":1}},\"operationName\":\"CommunityMomentCallout_Claim\",\"variables\":{\"input\":{\"momentID\":\"%s\"}}}";
	
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
		var expected = GQLResponse.<CommunityMomentCalloutClaimData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 9,
						"operationName", "CommunityMomentCallout_Claim",
						"requestID", "request-id"
				))
				.data(CommunityMomentCalloutClaimData.builder()
						.moment(ClaimCommunityMomentPayload.builder()
								.moment(CommunityMoment.builder()
										.id(MOMENT_ID)
										.build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(MOMENT_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/communityMomentCalloutClaim_success.json"))
				.withStatus(200);
		
		assertThat(tested.claimCommunityMoment(MOMENT_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(MOMENT_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.claimCommunityMoment(MOMENT_ID));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(MOMENT_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidRequest.json"))
				.withStatus(200);
		
		assertThat(tested.claimCommunityMoment(MOMENT_ID)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(MOMENT_ID))
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.claimCommunityMoment(MOMENT_ID)).isEmpty();
		
		unirest.verifyAll();
	}
}