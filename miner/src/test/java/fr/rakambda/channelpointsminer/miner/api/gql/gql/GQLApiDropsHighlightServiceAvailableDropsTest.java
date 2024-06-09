package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Channel;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropBenefit;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropBenefitEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaign;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaignSummary;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Game;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDrop;
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
class GQLApiDropsHighlightServiceAvailableDropsTest extends AbstractGQLTest{
    private static final String STREAMER_ID = "streamer-id";
    
    @Test
    void nominalWithDrops(UnirestMock unirest){
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
                                .viewerDropCampaigns(List.of(DropCampaign.builder()
                                        .id("campaign-id")
                                        .endAt(ZonedDateTime.of(2021, 10, 11, 5, 0, 0, 0, UTC))
                                        .timeBasedDrops(List.of(TimeBasedDrop.builder()
                                                .id("drop-id")
                                                .name("drop-name")
                                                .startAt(ZonedDateTime.of(2021, 10, 4, 15, 0, 0, 0, UTC))
                                                .endAt(ZonedDateTime.of(2021, 10, 11, 5, 0, 0, 0, UTC))
                                                .benefitEdges(List.of(DropBenefitEdge.builder()
		                                                .benefit(DropBenefit.builder()
				                                                .id("benefit-id")
				                                                .build())
                                                        .entitlementLimit(1)
                                                        .build()))
                                                .build()))
				                                .summary(DropCampaignSummary.builder()
						                                .includesSubRequirement(true)
						                                .build())
                                        .build()))
                                .build())
                        .build())
                .build();
        
        expectValidRequestOkWithIntegrityOk("api/gql/gql/dropsHighlightServiceAvailableDrops_withDrops.json");
        
        assertThat(tested.dropsHighlightServiceAvailableDrops(STREAMER_ID)).contains(expected);
        
        verifyAll();
    }
    
    @Test
    void nominalNoDrops(){
        var expected = GQLResponse.<DropsHighlightServiceAvailableDropsData> builder()
                .extensions(Map.of(
                        "durationMilliseconds", 31,
                        "operationName", "DropsHighlightService_AvailableDrops",
                        "requestID", "request-id"
                ))
                .data(DropsHighlightServiceAvailableDropsData.builder()
                        .channel(Channel.builder()
                                .build())
                        .build())
                .build();
        
        expectValidRequestOkWithIntegrityOk("api/gql/gql/dropsHighlightServiceAvailableDrops_noDrops.json");
        
        assertThat(tested.dropsHighlightServiceAvailableDrops(STREAMER_ID)).contains(expected);
        
        verifyAll();
    }
    
    @Override
    protected String getValidRequest(){
	    return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"962510a535f25f33bbf85d7767982e3bb6d1b00f84dd3c7a06d8572323dfd010\",\"version\":1}},\"operationName\":\"DropsHighlightService_AvailableDrops\",\"variables\":{\"channelID\":\"%s\"}}".formatted(STREAMER_ID);
    }
}