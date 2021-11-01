package fr.raksrinana.twitchminer.api.gql;

import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.twitchminer.api.gql.data.types.*;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import fr.raksrinana.twitchminer.tests.TestUtils;
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
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import static java.time.ZoneOffset.UTC;
import static kong.unirest.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiDropsHighlightServiceAvailableDropsTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String STREAMER_ID = "streamer-id";
	private static final String VALID_QUERY = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"b19ee96a0e79e3f8281c4108bc4c7b3f232266db6f96fd04a339ab393673a075\",\"version\":1}},\"operationName\":\"DropsHighlightService_AvailableDrops\",\"variables\":{\"channelID\":\"%s\"}}";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void nominalWithDrops(MockClient unirest) throws MalformedURLException{
		var game = Game.builder()
				.id("159357")
				.name("game-name")
				.build();
		var expected = GQLResponse.<DropsHighlightServiceAvailableDropsData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 31,
						"operationName", "DropsHighlightService_AvailableDrops",
						"requestID", "request-id"
				))
				.data(DropsHighlightServiceAvailableDropsData.builder()
						.channel(Channel.builder()
								.id("123456789")
								.viewerDropCampaigns(List.of(DropCampaign.builder()
										.id("campaign-id")
										.name("campaign-name")
										.game(game)
										.detailsUrl(new URL("https://google.com/campaign-info"))
										.endAt(ZonedDateTime.of(2021, 10, 11, 5, 0, 0, 0, UTC))
										.imageUrl(new URL("https://google.com/campaign-image"))
										.timeBasedDrops(List.of(TimeBasedDrop.builder()
														.id("drop-id")
														.name("drop-name")
														.startAt(ZonedDateTime.of(2021,10,4,15,0,0,0,UTC))
														.endAt(ZonedDateTime.of(2021,10,11,5,0,0,0,UTC))
														.benefitEdges(List.of(DropBenefitEdge.builder()
																		.benefit(DropBenefit.builder()
																				.id("benefit-id")
																				.name("benefit-name")
																				.game(game)
																				.imageAssetUrl(new URL("https://google.com/drop-image"))
																				.build())
																		.entitlementLimit(1)
																.build()))
														.requiredMinutesWatched(240)
												.build()))
										.build()))
								.build())
						.currentUser(User.builder()
								.id("987654321")
								.inventory(Inventory.builder().build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(STREAMER_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/dropsHighlightServiceAvailableDrops_withDrops.json"))
				.withStatus(200);
		
		assertThat(tested.dropsHighlightServiceAvailableDrops(STREAMER_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void nominalNoDrops(MockClient unirest){
		var expected = GQLResponse.<DropsHighlightServiceAvailableDropsData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 31,
						"operationName", "DropsHighlightService_AvailableDrops",
						"requestID", "request-id"
				))
				.data(DropsHighlightServiceAvailableDropsData.builder()
						.channel(Channel.builder()
								.id("123456789")
								.build())
						.currentUser(User.builder()
								.id("987654321")
								.inventory(Inventory.builder().build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(STREAMER_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/dropsHighlightServiceAvailableDrops_noDrops.json"))
				.withStatus(200);
		
		assertThat(tested.dropsHighlightServiceAvailableDrops(STREAMER_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(STREAMER_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.dropsHighlightServiceAvailableDrops(STREAMER_ID));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(STREAMER_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidRequest.json"))
				.withStatus(200);
		
		assertThat(tested.dropsHighlightServiceAvailableDrops(STREAMER_ID)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(STREAMER_ID))
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.dropsHighlightServiceAvailableDrops(STREAMER_ID)).isEmpty();
		
		unirest.verifyAll();
	}
}