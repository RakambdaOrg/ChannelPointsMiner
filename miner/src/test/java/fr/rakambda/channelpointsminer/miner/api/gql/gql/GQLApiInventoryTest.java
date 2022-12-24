package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.inventory.InventoryData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Channel;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropBenefit;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropBenefitEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaign;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaignACL;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaignSelfEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaignStatus;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Game;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Inventory;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDrop;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDropSelfEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.UserDropReward;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiInventoryTest extends AbstractGQLTest{
	
	@Test
	void nominal(UnirestMock unirest) throws MalformedURLException{
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
														.requiredAccountLink("https://link-ev-account")
														.totalCount(1)
														.build()
										))
										.build())
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/inventory.json");
		
		assertThat(tested.inventory()).isPresent().get().isEqualTo(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"e0765ebaa8e8eeb4043cc6dfeab3eac7f682ef5f724b81367e6e55c7aef2be4c\",\"version\":1}},\"operationName\":\"Inventory\",\"variables\":{}}";
	}
}