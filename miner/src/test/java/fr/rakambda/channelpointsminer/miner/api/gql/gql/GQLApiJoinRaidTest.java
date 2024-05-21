package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.joinraid.JoinRaidData;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiJoinRaidTest extends AbstractGQLTest{
	private static final String RAID_ID = "raid-id";
	
	@Test
	void nominalFollowRaid(){
		var expected = GQLResponse.<JoinRaidData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 4,
						"operationName", "JoinRaid",
						"requestID", "request-id"
				))
				.data(JoinRaidData.builder()
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/joinRaid.json");
		
		assertThat(tested.joinRaid(RAID_ID)).contains(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"c6a332a86d1087fbbb1a8623aa01bd1313d2386e7c63be60fdb2d1901f01a4ae\",\"version\":1}},\"operationName\":\"JoinRaid\",\"variables\":{\"input\":{\"raidID\":\"%s\"}}}".formatted(RAID_ID);
	}
}