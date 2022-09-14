package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.dropspageclaimdroprewards.DropsPageClaimDropRewardsData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.ClaimDropRewardsPayload;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.ClaimDropRewardsStatus;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMock;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiDropsPageClaimDropRewardsTest extends AbstractGQLTest{
	private static final String DROP_ID = "drop-id";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void nominal(UnirestMock unirest) throws MalformedURLException{
		var expected = GQLResponse.<DropsPageClaimDropRewardsData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 18,
						"operationName", "DropsPage_ClaimDropRewards",
						"requestID", "request-id"
				))
				.data(DropsPageClaimDropRewardsData.builder()
						.claimDropRewards(ClaimDropRewardsPayload.builder()
								.status(ClaimDropRewardsStatus.ELIGIBLE_FOR_ALL)
								.isUserAccountConnected(false)
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk(unirest, "api/gql/dropspageclaimdroprewardsdata_eligibleforall.json");
		
		assertThat(tested.dropsPageClaimDropRewards(DROP_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"2f884fa187b8fadb2a49db0adc033e636f7b6aaee6e76de1e2bba9a7baf0daf6\",\"version\":1}},\"operationName\":\"DropsPage_ClaimDropRewards\",\"variables\":{\"input\":{\"dropInstanceID\":\"%s\"}}}".formatted(DROP_ID);
	}
}