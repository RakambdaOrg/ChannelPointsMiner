package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.claimcommunitypoints.ClaimCommunityPointsData;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiClaimCommunityPointsTest extends AbstractGQLTest{
	private static final String CHANNEL_ID = "channel-id";
	private static final String CLAIM_ID = "claim-id";
	
	@Test
	void nominalClaimed(){
		var expected = GQLResponse.<ClaimCommunityPointsData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 55,
						"operationName", "ClaimCommunityPoints",
						"requestID", "request-id"
				))
				.data(ClaimCommunityPointsData.builder()
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/claimCommunityPoints_claimed.json");
		
		assertThat(tested.claimCommunityPoints(CHANNEL_ID, CLAIM_ID)).contains(expected);
		
		verifyAll();
	}
	
	@Test
	void nominalNotFound(){
		var expected = GQLResponse.<ClaimCommunityPointsData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 7,
						"operationName", "ClaimCommunityPoints",
						"requestID", "request-id"
				))
				.data(ClaimCommunityPointsData.builder()
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/claimCommunityPoints_notFound.json");
		
		assertThat(tested.claimCommunityPoints(CHANNEL_ID, CLAIM_ID)).contains(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"46aaeebe02c99afdf4fc97c7c0cba964124bf6b0af229395f1f6d1feed05b3d0\",\"version\":1}},\"operationName\":\"ClaimCommunityPoints\",\"variables\":{\"input\":{\"channelID\":\"%s\",\"claimID\":\"%s\"}}}".formatted(CHANNEL_ID, CLAIM_ID);
	}
}