package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Stream;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.withislive.WithIsStreamLiveData;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiWithIsStreamLiveTest extends AbstractGQLTest{
	private static final String CHANNEL_ID = "channel-id";
	
	@Test
	void nominalLive(){
		var expected = GQLResponse.<WithIsStreamLiveData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 58,
						"operationName", "WithIsStreamLiveQuery",
						"requestID", "request-id"
				))
				.data(WithIsStreamLiveData.builder()
						.user(User.builder()
								.id("987654321")
								.stream(Stream.builder()
										.id("12345")
										.build())
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/withisstreamlive_live.json");
		
		assertThat(tested.withIsStreamLive(CHANNEL_ID)).contains(expected);
		
		verifyAll();
	}
	
	@Test
	void nominalNotLive(){
		var expected = GQLResponse.<WithIsStreamLiveData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 58,
						"operationName", "WithIsStreamLiveQuery",
						"requestID", "request-id"
				))
				.data(WithIsStreamLiveData.builder()
						.user(User.builder()
								.id("987654321")
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/withisstreamlive_not_live.json");
		
		assertThat(tested.withIsStreamLive(CHANNEL_ID)).contains(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"04e46329a6786ff3a81c01c50bfa5d725902507a0deb83b0edbf7abe7a3716ea\",\"version\":1}},\"operationName\":\"WithIsStreamLiveQuery\",\"variables\":{\"id\":\"%s\"}}".formatted(CHANNEL_ID);
	}
}