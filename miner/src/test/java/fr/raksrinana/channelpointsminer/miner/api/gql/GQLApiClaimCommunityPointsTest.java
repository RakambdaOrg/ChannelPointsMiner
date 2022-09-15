package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.claimcommunitypoints.ClaimCommunityPointsData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.ClaimCommunityPointsError;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.ClaimCommunityPointsPayload;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.CommunityPointsClaim;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static fr.raksrinana.channelpointsminer.miner.api.gql.data.types.ClaimErrorCode.NOT_FOUND;
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
		
		expectValidRequestOkWithIntegrityOk("api/gql/claimCommunityPoints_claimed.json");
		
		assertThat(tested.claimCommunityPoints(CHANNEL_ID, CLAIM_ID)).isPresent().get().isEqualTo(expected);
		
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
						.claimCommunityPoints(ClaimCommunityPointsPayload.builder()
								.error(ClaimCommunityPointsError.builder()
										.code(NOT_FOUND)
										.build())
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/claimCommunityPoints_notFound.json");
		
		assertThat(tested.claimCommunityPoints(CHANNEL_ID, CLAIM_ID)).isPresent().get().isEqualTo(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"46aaeebe02c99afdf4fc97c7c0cba964124bf6b0af229395f1f6d1feed05b3d0\",\"version\":1}},\"operationName\":\"ClaimCommunityPoints\",\"variables\":{\"input\":{\"channelID\":\"%s\",\"claimID\":\"%s\"}}}".formatted(CHANNEL_ID, CLAIM_ID);
	}
}