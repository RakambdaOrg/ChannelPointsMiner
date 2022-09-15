package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.communitymomentcalloutclaim.CommunityMomentCalloutClaimData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.ClaimCommunityMomentPayload;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.CommunityMoment;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiClaimCommunityMomentTest extends AbstractGQLTest{
	private static final String MOMENT_ID = "moment-id";
	
	@Test
	void nominalClaimed(){
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
		
		expectValidRequestOkWithIntegrityOk("api/gql/communityMomentCalloutClaim_success.json");
		
		assertThat(tested.claimCommunityMoment(MOMENT_ID)).isPresent().get().isEqualTo(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"e2d67415aead910f7f9ceb45a77b750a1e1d9622c936d832328a0689e054db62\",\"version\":1}},\"operationName\":\"CommunityMomentCallout_Claim\",\"variables\":{\"input\":{\"momentID\":\"%s\"}}}".formatted(MOMENT_ID);
	}
}