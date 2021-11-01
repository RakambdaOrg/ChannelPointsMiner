package fr.raksrinana.twitchminer.api.gql;

import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.channelpointscontext.ChannelPointsContextData;
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
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import static fr.raksrinana.twitchminer.api.gql.data.types.ContentId.SINGLE_MESSAGE_BYPASS_SUB_MODE;
import static fr.raksrinana.twitchminer.api.gql.data.types.ContentType.AUTOMATIC_REWARD;
import static fr.raksrinana.twitchminer.api.gql.data.types.ContentType.CUSTOM_REWARD;
import static fr.raksrinana.twitchminer.api.gql.data.types.RewardType.SEND_HIGHLIGHTED_MESSAGE;
import static java.time.ZoneOffset.UTC;
import static kong.unirest.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiChannelPointsContextTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String USERNAME = "username";
	private static final String VALID_QUERY = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"9988086babc615a918a1e9a722ff41d98847acac822645209ac7379eecb27152\",\"version\":1}},\"operationName\":\"ChannelPointsContext\",\"variables\":{\"channelLogin\":\"%s\"}}";
	
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
		var communityPointsImage = CommunityPointsImage.builder()
				.url(new URL("https://image"))
				.url2X(new URL("https://image2x"))
				.url4X(new URL("https://image4x"))
				.build();
		var expected = GQLResponse.<ChannelPointsContextData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 74,
						"operationName", "ChannelPointsContext",
						"requestID", "request-id"
				))
				.data(ChannelPointsContextData.builder()
						.community(User.builder()
								.id("987654321")
								.displayName("streamername")
								.channel(Channel.builder()
										.id("987654321")
										.self(ChannelSelfEdge.builder()
												.communityPoints(CommunityPointsProperties.builder()
														.balance(0)
														.canRedeemRewardsForFree(false)
														.lastViewedContent(List.of(
																CommunityPointsLastViewedContentByType.builder()
																		.contentType(AUTOMATIC_REWARD)
																		.lastViewedAt(ZonedDateTime.of(2021, 10, 5, 20, 59, 11, 67754116, UTC))
																		.build(),
																CommunityPointsLastViewedContentByType.builder()
																		.contentType(CUSTOM_REWARD)
																		.lastViewedAt(ZonedDateTime.of(2021, 10, 5, 20, 59, 11, 67754117, UTC))
																		.build()
														))
														.build())
												.build())
										.communityPointsSettings(CommunityPointsChannelSettings.builder()
												.name("points name")
												.image(communityPointsImage)
												.automaticRewards(List.of(
														CommunityPointsAutomaticReward.builder()
																.id("reward-id")
																.enabled(true)
																.hiddenForSubs(false)
																.defaultBackgroundColor(Color.decode("#FF6905"))
																.defaultCost(600)
																.defaultImage(communityPointsImage)
																.minimumCost(10)
																.type(SEND_HIGHLIGHTED_MESSAGE)
																.globallyUpdatedForIndicatorAt(ZonedDateTime.of(2019, 9, 3, 21, 0, 0, 0, UTC))
																.build()
												))
												.enabled(true)
												.raidPointAmount(250)
												.emoteVariants(List.of(
														CommunityPointsEmoteVariant.builder()
																.id("147258369")
																.unlockable(true)
																.emote(CommunityPointsEmote.builder()
																		.id("147258369")
																		.token("emotetoken")
																		.build())
																.modifications(List.of(
																		CommunityPointsEmoteModification.builder()
																				.id("147258369_BW")
																				.emote(CommunityPointsEmote.builder()
																						.id("147258369_BW")
																						.token("emotetoken_BW")
																						.build())
																				.modifierIconDark(communityPointsImage)
																				.modifierIconLight(communityPointsImage)
																				.title("Greyscale")
																				.globallyUpdatedForIndicatorAt(ZonedDateTime.of(2019, 9, 3, 21, 0, 0, 0, UTC))
																				.build()
																))
																.build()
												))
												.earning(CommunityPointsChannelEarningSettings.builder()
														.id("abcdefghijklmnopqrstuvwxyz")
														.averagePointsPerHour(220)
														.cheerPoints(350)
														.claimPoints(50)
														.followPoints(300)
														.passiveWatchPoints(10)
														.raidPoints(250)
														.subscriptionGiftPoints(500)
														.watchStreakPoints(List.of(
																CommunityPointsWatchStreakEarningSettings.builder().points(300).build(),
																CommunityPointsWatchStreakEarningSettings.builder().points(350).build(),
																CommunityPointsWatchStreakEarningSettings.builder().points(400).build(),
																CommunityPointsWatchStreakEarningSettings.builder().points(450).build()
														))
														.multipliers(List.of(
																CommunityPointsMultiplier.builder()
																		.reasonCode(MultiplierReasonCode.SUB_T1)
																		.factor(.2F)
																		.build(),
																CommunityPointsMultiplier.builder()
																		.reasonCode(MultiplierReasonCode.SUB_T2)
																		.factor(.4F)
																		.build(),
																CommunityPointsMultiplier.builder()
																		.reasonCode(MultiplierReasonCode.SUB_T3)
																		.factor(1F)
																		.build()
														))
														.build())
												.build())
										.build())
								.self(UserSelfConnection.builder()
										.moderator(false)
										.build())
								.build())
						.currentUser(User.builder()
								.id("123456789")
								.communityPoints(CommunityPointsUserProperties.builder()
										.lastViewedContent(List.of(
												CommunityPointsLastViewedContentByTypeAndID.builder()
														.contentId(SINGLE_MESSAGE_BYPASS_SUB_MODE)
														.contentType(AUTOMATIC_REWARD)
														.lastViewedAt(ZonedDateTime.of(2021, 10, 6, 19, 50, 35, 443714534, UTC))
														.build()
										))
										.build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/channelPointsContext_noClaim.json"))
				.withStatus(200);
		
		assertThat(tested.channelPointsContext(USERNAME)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void nominalWithClaim(MockClient unirest) throws MalformedURLException{
		var communityPointsImage = CommunityPointsImage.builder()
				.url(new URL("https://image"))
				.url2X(new URL("https://image2x"))
				.url4X(new URL("https://image4x"))
				.build();
		var expected = GQLResponse.<ChannelPointsContextData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 74,
						"operationName", "ChannelPointsContext",
						"requestID", "request-id"
				))
				.data(ChannelPointsContextData.builder()
						.community(User.builder()
								.id("987654321")
								.displayName("streamername")
								.channel(Channel.builder()
										.id("987654321")
										.self(ChannelSelfEdge.builder()
												.communityPoints(CommunityPointsProperties.builder()
														.availableClaim(CommunityPointsClaim.builder()
																.id("claim-id")
																.build())
														.balance(0)
														.canRedeemRewardsForFree(false)
														.lastViewedContent(List.of(
																CommunityPointsLastViewedContentByType.builder()
																		.contentType(AUTOMATIC_REWARD)
																		.lastViewedAt(ZonedDateTime.of(2021, 10, 5, 20, 59, 11, 67754116, UTC))
																		.build(),
																CommunityPointsLastViewedContentByType.builder()
																		.contentType(CUSTOM_REWARD)
																		.lastViewedAt(ZonedDateTime.of(2021, 10, 5, 20, 59, 11, 67754117, UTC))
																		.build()
														))
														.build())
												.build())
										.communityPointsSettings(CommunityPointsChannelSettings.builder()
												.name("points name")
												.image(communityPointsImage)
												.automaticRewards(List.of(
														CommunityPointsAutomaticReward.builder()
																.id("reward-id")
																.enabled(true)
																.hiddenForSubs(false)
																.defaultBackgroundColor(Color.decode("#FF6905"))
																.defaultCost(600)
																.defaultImage(communityPointsImage)
																.minimumCost(10)
																.type(SEND_HIGHLIGHTED_MESSAGE)
																.globallyUpdatedForIndicatorAt(ZonedDateTime.of(2019, 9, 3, 21, 0, 0, 0, UTC))
																.build()
												))
												.enabled(true)
												.raidPointAmount(250)
												.emoteVariants(List.of(
														CommunityPointsEmoteVariant.builder()
																.id("147258369")
																.unlockable(true)
																.emote(CommunityPointsEmote.builder()
																		.id("147258369")
																		.token("emotetoken")
																		.build())
																.modifications(List.of(
																		CommunityPointsEmoteModification.builder()
																				.id("147258369_BW")
																				.emote(CommunityPointsEmote.builder()
																						.id("147258369_BW")
																						.token("emotetoken_BW")
																						.build())
																				.modifierIconDark(communityPointsImage)
																				.modifierIconLight(communityPointsImage)
																				.title("Greyscale")
																				.globallyUpdatedForIndicatorAt(ZonedDateTime.of(2019, 9, 3, 21, 0, 0, 0, UTC))
																				.build()
																))
																.build()
												))
												.earning(CommunityPointsChannelEarningSettings.builder()
														.id("abcdefghijklmnopqrstuvwxyz")
														.averagePointsPerHour(220)
														.cheerPoints(350)
														.claimPoints(50)
														.followPoints(300)
														.passiveWatchPoints(10)
														.raidPoints(250)
														.subscriptionGiftPoints(500)
														.watchStreakPoints(List.of(
																CommunityPointsWatchStreakEarningSettings.builder().points(300).build(),
																CommunityPointsWatchStreakEarningSettings.builder().points(350).build(),
																CommunityPointsWatchStreakEarningSettings.builder().points(400).build(),
																CommunityPointsWatchStreakEarningSettings.builder().points(450).build()
														))
														.multipliers(List.of(
																CommunityPointsMultiplier.builder()
																		.reasonCode(MultiplierReasonCode.SUB_T1)
																		.factor(.2F)
																		.build(),
																CommunityPointsMultiplier.builder()
																		.reasonCode(MultiplierReasonCode.SUB_T2)
																		.factor(.4F)
																		.build(),
																CommunityPointsMultiplier.builder()
																		.reasonCode(MultiplierReasonCode.SUB_T3)
																		.factor(1F)
																		.build()
														))
														.build())
												.build())
										.build())
								.self(UserSelfConnection.builder()
										.moderator(false)
										.build())
								.build())
						.currentUser(User.builder()
								.id("123456789")
								.communityPoints(CommunityPointsUserProperties.builder()
										.lastViewedContent(List.of(
												CommunityPointsLastViewedContentByTypeAndID.builder()
														.contentId(SINGLE_MESSAGE_BYPASS_SUB_MODE)
														.contentType(AUTOMATIC_REWARD)
														.lastViewedAt(ZonedDateTime.of(2021, 10, 6, 19, 50, 35, 443714534, UTC))
														.build()
										))
										.build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/channelPointsContext_withClaim.json"))
				.withStatus(200);
		
		assertThat(tested.channelPointsContext(USERNAME)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.channelPointsContext(USERNAME));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidRequest.json"))
				.withStatus(200);
		
		assertThat(tested.channelPointsContext(USERNAME)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.channelPointsContext(USERNAME)).isEmpty();
		
		unirest.verifyAll();
	}
}