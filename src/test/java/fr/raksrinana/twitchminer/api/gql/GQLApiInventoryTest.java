package fr.raksrinana.twitchminer.api.gql;

import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.inventory.InventoryData;
import fr.raksrinana.twitchminer.api.gql.data.types.*;
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
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import static fr.raksrinana.twitchminer.tests.TestUtils.getAllResourceContent;
import static java.time.ZoneOffset.UTC;
import static kong.unirest.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiInventoryTest{
	public static final String VALID_QUERY = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"e0765ebaa8e8eeb4043cc6dfeab3eac7f682ef5f724b81367e6e55c7aef2be4c\",\"version\":1}},\"operationName\":\"Inventory\",\"variables\":{}}";
	private static final String ACCESS_TOKEN = "access-token";
	
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
		var expected = GQLResponse.<InventoryData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 120,
						"operationName", "Inventory",
						"requestID", "request-id"
				))
				.data(InventoryData.builder()
						.currentUser(User.builder()
								.id("123456789")
								.inventory(Inventory.builder()
										.dropCampaignsInProgress(List.of(
												DropCampaign.builder()
														.id("campaign-1")
														.accountLinkUrl(new URL("https://link1"))
														.startAt(ZonedDateTime.of(2021, 10, 21, 16, 0, 0, 0, UTC))
														.endAt(ZonedDateTime.of(2021, 11, 11, 2, 0, 0, 0, UTC))
														.imageUrl(new URL("https://image-1"))
														.name("campaign name 1")
														.status(DropCampaignStatus.ACTIVE)
														.self(DropCampaignSelfEdge.builder()
																.isAccountConnected(true)
																.build())
														.game(game)
														.allow(DropCampaignACL.builder().build())
														.timeBasedDrops(List.of(
																TimeBasedDrop.builder()
																		.id("drop-1")
																		.name("drop name 1")
																		.startAt(ZonedDateTime.of(2021, 10, 21, 16, 0, 0, 0, UTC))
																		.endAt(ZonedDateTime.of(2021, 10, 28, 16, 0, 0, 0, UTC))
																		.requiredMinutesWatched(90)
																		.preconditionDrops(List.of(
																				TimeBasedDrop.builder()
																						.id("required-drop-id")
																						.build()
																		))
																		.benefitEdges(List.of(
																				DropBenefitEdge.builder()
																						.benefit(DropBenefit.builder()
																								.id("benefit-1")
																								.imageAssetUrl(new URL("https://benefit-1-img"))
																								.name("benefit 1 name")
																								.build())
																						.entitlementLimit(2)
																						.claimCount(1)
																						.build()
																		))
																		.self(TimeBasedDropSelfEdge.builder()
																				.hasPreconditionsMet(true)
																				.currentMinutesWatched(10)
																				.isClaimed(false)
																				.build())
																		.campaign(DropCampaign.builder()
																				.id("campaign-sub-1")
																				.accountLinkUrl(new URL("https://link-sub"))
																				.self(DropCampaignSelfEdge.builder()
																						.isAccountConnected(false)
																						.build())
																				.build())
																		.build()
														))
														.build(),
												DropCampaign.builder()
														.id("campaign-2")
														.accountLinkUrl(new URL("https://link-2"))
														.startAt(ZonedDateTime.of(2021, 10, 16, 7, 0, 0, 0, UTC))
														.endAt(ZonedDateTime.of(2021, 11, 7, 23, 30, 0, 0, UTC))
														.imageUrl(new URL("https://image2"))
														.name("campaign name 2")
														.status(DropCampaignStatus.ACTIVE)
														.self(DropCampaignSelfEdge.builder()
																.isAccountConnected(false)
																.build())
														.game(game)
														.allow(DropCampaignACL.builder()
																.channels(List.of(
																		Channel.builder()
																				.id("456789")
																				.name("channel 1")
																				.url(new URL("https://channel_1"))
																				.build(),
																		Channel.builder()
																				.id("987654")
																				.name("channel 2")
																				.url(new URL("https://channel_2"))
																				.build()
																))
																.build())
														.build()
										))
										.gameEventDrops(List.of(
												UserDropReward.builder()
														.game(Game.builder()
																.id("159")
																.name("ev-drop-game-name")
																.build())
														.id("reward-id")
														.imageUrl(new URL("https://reward-image"))
														.isConnected(false)
														.lastAwardedAt(ZonedDateTime.of(2021, 10, 31, 21, 41, 34, 0, UTC))
														.name("ev reward name")
														.requiredAccountLink(new URL("https://link-ev-account"))
														.totalCount(1)
														.build()
										))
										.build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY)
				.thenReturn(getAllResourceContent("api/gql/inventory.json"))
				.withStatus(200);
		
		assertThat(tested.inventory()).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY)
				.thenReturn(getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.inventory());
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY)
				.thenReturn(getAllResourceContent("api/gql/invalidRequest.json"))
				.withStatus(200);
		
		assertThat(tested.inventory()).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY)
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.inventory()).isEmpty();
		
		unirest.verifyAll();
	}
}