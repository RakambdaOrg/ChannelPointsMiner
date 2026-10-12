package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropchannelcampaignsprogress.DropChannelCampaignsProgressData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsCampaign;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsProgressCriteria;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsRewardGroup;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsRewardGroupSelfEdge;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiDropChannelCampaignsProgressTest extends AbstractGQLTest{
    private static final String STREAMER_ID = "streamer-id";

    @Test
    void nominal(){
        var expected = GQLResponse.<DropChannelCampaignsProgressData> builder()
                .extensions(Map.of(
                        "durationMilliseconds", 31,
                        "operationName", "DropChannelCampaignsProgress",
                        "requestID", "request-id"
                ))
                .data(DropChannelCampaignsProgressData.builder()
                        .channelDropCampaignsProgress(List.of(
                                DropsCampaign.builder()
                                        .id("1234567890")
                                        .startAt(ZonedDateTime.of(2026, 9, 23, 14, 0, 0, 0, UTC))
                                        .endAt(ZonedDateTime.of(2026, 9, 13, 17, 59, 59, 999_000_000, UTC))
                                        .permanentlyDismissible(false)
                                        .rewardGroups(List.of(DropsRewardGroup.builder()
                                                .id("123789")
                                                .progressCriteria(DropsProgressCriteria.builder()
                                                        .requirementType("SUB")
                                                        .autoProgressable(true)
                                                        .build())
                                                .self(DropsRewardGroupSelfEdge.builder()
                                                        .status("IN_PROGRESS")
                                                        .build())
                                                .build()))
                                        .build(),
                                DropsCampaign.builder()
                                        .id("65320")
                                        .startAt(ZonedDateTime.of(2026, 9, 23, 12, 53, 54, 0, UTC))
                                        .endAt(ZonedDateTime.of(2026, 10, 18, 18, 59, 59, 999_000_000, UTC))
                                        .permanentlyDismissible(false)
                                        .rewardGroups(List.of(DropsRewardGroup.builder()
                                                .id("32432")
                                                .progressCriteria(DropsProgressCriteria.builder()
                                                        .requirementType("WATCH")
                                                        .autoProgressable(true)
                                                        .build())
                                                .self(DropsRewardGroupSelfEdge.builder()
                                                        .status("CLAIMED")
                                                        .build())
                                                .build()))
                                        .build()
                        ))
                        .build())
                .build();

        expectValidRequestOkWithIntegrityOk("api/gql/gql/dropChannelCampaignsProgress_ok.json");

        assertThat(tested.dropChannelCampaignsProgress(STREAMER_ID)).contains(expected);

        verifyAll();
    }

    @Override
    protected String getValidRequest(){
        return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"a9778563e6f7ef878891dcc4049c263ec376c1ef5c1ad69395c3d8aef7f455e4\",\"version\":1}},\"operationName\":\"DropChannelCampaignsProgress\",\"variables\":{\"channelID\":\"%s\"}}".formatted(STREAMER_ID);
    }
}
