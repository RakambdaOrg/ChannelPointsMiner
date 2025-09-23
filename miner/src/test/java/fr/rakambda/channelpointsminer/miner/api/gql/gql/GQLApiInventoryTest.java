package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.inventory.InventoryData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropBenefit;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropBenefitEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaign;
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
														.startAt(ZonedDateTime.of(2021, 10, 21, 16, 0, 0, 0, UTC))
														.endAt(ZonedDateTime.of(2021, 11, 11, 2, 0, 0, 0, UTC))
														.timeBasedDrops(List.of(
																TimeBasedDrop.builder()
																		.id("drop-1")
																		.name("drop name 1")
																		.startAt(ZonedDateTime.of(2021, 10, 21, 16, 0, 0, 0, UTC))
																		.endAt(ZonedDateTime.of(2021, 10, 28, 16, 0, 0, 0, UTC))
																		.benefitEdges(List.of(
																				DropBenefitEdge.builder()
																						.benefit(DropBenefit.builder()
																								.id("benefit-1")
																								.build())
																						.entitlementLimit(2)
																						.claimCount(1)
																						.build()
																		))
																		.self(TimeBasedDropSelfEdge.builder()
																				.isClaimed(false)
																				.build())
																		.build()
														))
														.build(),
												DropCampaign.builder()
														.id("campaign-2")
														.startAt(ZonedDateTime.of(2021, 10, 16, 7, 0, 0, 0, UTC))
														.endAt(ZonedDateTime.of(2021, 11, 7, 23, 30, 0, 0, UTC))
														.build()
										))
										.gameEventDrops(List.of(
												UserDropReward.builder()
														.id("reward-id")
														.totalCount(1)
														.lastAwardedAt(ZonedDateTime.of(2021, 10, 31, 21, 41, 34, 0, UTC))
														.build()
										))
										.build())
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/inventory.json");
		
		assertThat(tested.inventory()).contains(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"d86775d0ef16a63a33ad52e80eaff963b2d5b72fada7c991504a57496e1d8e4b\",\"version\":1}},\"operationName\":\"Inventory\",\"variables\":{\"fetchRewardCampaigns\":true}}";
	}
}