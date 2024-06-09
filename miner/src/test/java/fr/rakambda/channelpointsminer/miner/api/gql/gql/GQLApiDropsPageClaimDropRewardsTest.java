package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropspageclaimdroprewards.DropsPageClaimDropRewardsData;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiDropsPageClaimDropRewardsTest extends AbstractGQLTest{
	private static final String DROP_ID = "drop-id";
	
	@Test
	void nominal(UnirestMock unirest) throws MalformedURLException{
		var expected = GQLResponse.<DropsPageClaimDropRewardsData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 18,
						"operationName", "DropsPage_ClaimDropRewards",
						"requestID", "request-id"
				))
				.data(DropsPageClaimDropRewardsData.builder()
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/dropspageclaimdroprewardsdata_eligibleforall.json");
		
		assertThat(tested.dropsPageClaimDropRewards(DROP_ID)).contains(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"a455deea71bdc9015b78eb49f4acfbce8baa7ccbedd28e549bb025bd0f751930\",\"version\":1}},\"operationName\":\"DropsPage_ClaimDropRewards\",\"variables\":{\"input\":{\"dropInstanceID\":\"%s\"}}}".formatted(DROP_ID);
	}
}