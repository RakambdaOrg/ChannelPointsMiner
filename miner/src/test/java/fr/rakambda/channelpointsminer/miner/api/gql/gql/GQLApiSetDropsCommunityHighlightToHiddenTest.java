package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.setdropscommunityhighlighttohidden.SetDropsCommunityHighlightToHiddenData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.SetDropsCommunityHighlightToHiddenPayload;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiSetDropsCommunityHighlightToHiddenTest extends AbstractGQLTest{
	private static final String CAMPAIGN_ID = "campaign-id";
	private static final String CHANNEL_ID = "channel-id";
	
	@Test
	void nominalClaimed(){
		var expected = GQLResponse.<SetDropsCommunityHighlightToHiddenData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 9,
						"operationName", "SetDropsCommunityHighlightToHidden",
						"requestID", "request-id"
				))
				.data(SetDropsCommunityHighlightToHiddenData.builder()
						.setDropsCommunityHighlightToHiddenPayload(SetDropsCommunityHighlightToHiddenPayload.builder()
								.isHidden(true)
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/setDropsCommunityHighlightToHidden_success.json");
		
		var actual = tested.setDropsCommunityHighlightToHidden(CHANNEL_ID, CAMPAIGN_ID);
		assertThat(actual).contains(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"9dfaa13dbc7d178a35c0a038a270fad4b7bc1d0e1d404a18aed9b26ee797a697\",\"version\":1}},\"operationName\":\"SetDropsCommunityHighlightToHidden\",\"variables\":{\"input\":{\"campaignID\":\"%s\",\"channelID\":\"%s\"}}}".formatted(CAMPAIGN_ID, CHANNEL_ID);
	}
}