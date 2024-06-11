package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Channel;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ChannelSelfEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsClaim;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsProperties;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class GQLApiChannelPointsContextTest extends AbstractGQLTest{
	private static final String USERNAME = "username";
	
	@Test
	void nominal(){
		var expected = GQLResponse.<ChannelPointsContextData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 74,
						"operationName", "ChannelPointsContext",
						"requestID", "request-id"
				))
				.data(ChannelPointsContextData.builder()
						.community(User.builder()
								.id("987654321")
								.channel(Channel.builder()
										.self(ChannelSelfEdge.builder()
												.communityPoints(CommunityPointsProperties.builder()
														.balance(0)
														.build())
												.build())
										.build())
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/channelPointsContext_noClaim.json");
		
		assertThat(tested.channelPointsContext(USERNAME)).contains(expected);
		
		verifyAll();
	}
	
	@Test
	void nominalWithClaim(UnirestMock unirest){
		var expected = GQLResponse.<ChannelPointsContextData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 74,
						"operationName", "ChannelPointsContext",
						"requestID", "request-id"
				))
				.data(ChannelPointsContextData.builder()
						.community(User.builder()
								.id("987654321")
								.channel(Channel.builder()
										.self(ChannelSelfEdge.builder()
												.communityPoints(CommunityPointsProperties.builder()
														.availableClaim(CommunityPointsClaim.builder()
																.id("claim-id")
																.build())
														.balance(0)
														.build())
												.build())
										.build())
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/channelPointsContext_withClaim.json");
		
		assertThat(tested.channelPointsContext(USERNAME)).contains(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"1530a003a7d374b0380b79db0be0534f30ff46e61cffa2bc0e2468a909fbc024\",\"version\":1}},\"operationName\":\"ChannelPointsContext\",\"variables\":{\"channelLogin\":\"%s\",\"includeGoalTypes\":[\"CREATOR\",\"BOOST\"]}}".formatted(USERNAME);
	}
}