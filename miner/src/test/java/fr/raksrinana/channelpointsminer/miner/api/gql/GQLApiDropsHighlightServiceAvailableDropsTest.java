package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.Channel;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.DropBenefit;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.DropBenefitEdge;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.DropCampaign;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.Game;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.Inventory;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.TimeBasedDrop;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.User;
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
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiDropsHighlightServiceAvailableDropsTest extends AbstractGQLTest{
	private static final String STREAMER_ID = "streamer-id";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void nominalWithDrops(UnirestMock unirest) throws MalformedURLException{
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
												.startAt(ZonedDateTime.of(2021, 10, 4, 15, 0, 0, 0, UTC))
												.endAt(ZonedDateTime.of(2021, 10, 11, 5, 0, 0, 0, UTC))
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
		
		expectValidRequestOkWithIntegrityOk(unirest, "api/gql/dropsHighlightServiceAvailableDrops_withDrops.json");
		
		assertThat(tested.dropsHighlightServiceAvailableDrops(STREAMER_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void nominalNoDrops(UnirestMock unirest){
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
		
		expectValidRequestOkWithIntegrityOk(unirest, "api/gql/dropsHighlightServiceAvailableDrops_noDrops.json");
		
		assertThat(tested.dropsHighlightServiceAvailableDrops(STREAMER_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"b19ee96a0e79e3f8281c4108bc4c7b3f232266db6f96fd04a339ab393673a075\",\"version\":1}},\"operationName\":\"DropsHighlightService_AvailableDrops\",\"variables\":{\"channelID\":\"%s\"}}".formatted(STREAMER_ID);
	}
}