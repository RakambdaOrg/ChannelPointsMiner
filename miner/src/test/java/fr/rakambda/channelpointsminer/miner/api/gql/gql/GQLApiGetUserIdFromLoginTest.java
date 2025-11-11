package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.reportmenuitem.GetUserIdFromLoginData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Stream;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiGetUserIdFromLoginTest extends AbstractGQLTest{
	private static final String USERNAME = "username";
	
	@Test
	void nominal(){
		var expected = GQLResponse.<GetUserIdFromLoginData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 41,
						"operationName", "GetUserIDFromLogin",
						"requestID", "request-id"
				))
				.data(GetUserIdFromLoginData.builder()
						.user(User.builder()
								.id("123456789")
								.build())
						.build())
				.build();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/getUserIdFromLogin_offline.json");
		
		assertThat(tested.getUserIdFromLogin(USERNAME)).contains(expected);
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"c8502d09d4f290bb5155e6953a2c3119d4296d7ce647a2e21d1cf4c805583e43\",\"version\":1}},\"operationName\":\"GetUserIDFromLogin\",\"variables\":{\"lookupType\":\"ACTIVE\",\"login\":\"%s\"}}".formatted(USERNAME);
	}
}