package fr.raksrinana.twitchminer.api.gql;

import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.dropspageclaimdroprewards.DropsPageClaimDropRewardsData;
import fr.raksrinana.twitchminer.api.gql.data.types.ClaimDropRewardsPayload;
import fr.raksrinana.twitchminer.api.gql.data.types.ClaimDropRewardsStatus;
import fr.raksrinana.twitchminer.api.gql.data.types.Game;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import fr.raksrinana.twitchminer.tests.UnirestMockExtension;
import kong.unirest.MockClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import static fr.raksrinana.twitchminer.tests.TestUtils.getAllResourceContent;
import static kong.unirest.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiDropsPageClaimDropRewardsTest{
	private static final String VALID_QUERY = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"2f884fa187b8fadb2a49db0adc033e636f7b6aaee6e76de1e2bba9a7baf0daf6\",\"version\":1}},\"operationName\":\"DropsPage_ClaimDropRewards\",\"variables\":{\"input\":{\"dropInstanceID\":\"%s\"}}}";
	private static final String ACCESS_TOKEN = "access-token";
	private static final String DROP_ID = "";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void nominal(MockClient unirest) throws MalformedURLException{
		var game = Game.builder()
				.id("123")
				.name("game-1")
				.boxArtUrl(new URL("https://bow-art-1"))
				.build();
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
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(DROP_ID))
				.thenReturn(getAllResourceContent("api/gql/dropspageclaimdroprewardsdata_eligibleforall.json"))
				.withStatus(200);
		
		assertThat(tested.dropsPageClaimDropRewards(DROP_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(DROP_ID))
				.thenReturn(getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.dropsPageClaimDropRewards(DROP_ID));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(DROP_ID))
				.thenReturn(getAllResourceContent("api/gql/invalidRequest.json"))
				.withStatus(200);
		
		assertThat(tested.dropsPageClaimDropRewards(DROP_ID)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(DROP_ID))
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.dropsPageClaimDropRewards(DROP_ID)).isEmpty();
		
		unirest.verifyAll();
	}
}