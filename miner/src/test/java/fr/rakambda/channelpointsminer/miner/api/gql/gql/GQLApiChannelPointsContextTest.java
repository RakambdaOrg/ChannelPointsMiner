package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Channel;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ChannelSelfEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsAutomaticReward;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsChannelEarningSettings;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsChannelSettings;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsClaim;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsEmote;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsEmoteModification;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsEmoteModifier;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsEmoteVariant;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsImage;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsLastViewedContentByType;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsLastViewedContentByTypeAndID;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsMultiplier;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsProperties;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsUserProperties;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsWatchStreakEarningSettings;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.MultiplierReasonCode;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.UserSelfConnection;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import static fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ContentId.SINGLE_MESSAGE_BYPASS_SUB_MODE;
import static fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ContentType.AUTOMATIC_REWARD;
import static fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ContentType.CUSTOM_REWARD;
import static fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.RewardType.SEND_HIGHLIGHTED_MESSAGE;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

class GQLApiChannelPointsContextTest extends AbstractGQLTest{
    private static final String USERNAME = "username";
    
    @Test
    void nominal() throws MalformedURLException{
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
                                                                .type(6)
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
        
        expectValidRequestOkWithIntegrityOk("api/gql/gql/channelPointsContext_noClaim.json");
        
        assertThat(tested.channelPointsContext(USERNAME)).isPresent().get().isEqualTo(expected);
        
        verifyAll();
    }
    
    @Test
    void nominalWithClaim(UnirestMock unirest) throws MalformedURLException{
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
                                                                .type(6)
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
                                                                                .modifier(CommunityPointsEmoteModifier.builder()
                                                                                        .id("MOD_BW")
                                                                                        .build())
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
        
        expectValidRequestOkWithIntegrityOk("api/gql/gql/channelPointsContext_withClaim.json");
        
        assertThat(tested.channelPointsContext(USERNAME)).isPresent().get().isEqualTo(expected);
        
        verifyAll();
    }
    
    @Override
    protected String getValidRequest(){
        return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"1530a003a7d374b0380b79db0be0534f30ff46e61cffa2bc0e2468a909fbc024\",\"version\":1}},\"operationName\":\"ChannelPointsContext\",\"variables\":{\"channelLogin\":\"%s\",\"includeGoalTypes\":[\"CREATOR\",\"BOOST\"]}}".formatted(USERNAME);
    }
}