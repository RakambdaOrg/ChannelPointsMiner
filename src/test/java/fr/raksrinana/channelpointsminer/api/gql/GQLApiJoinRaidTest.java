package fr.raksrinana.channelpointsminer.api.gql;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.joinraid.JoinRaidData;
import fr.raksrinana.channelpointsminer.api.gql.data.types.JoinRaidPayload;
import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.tests.UnirestMockExtension;
import kong.unirest.core.MockClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static fr.raksrinana.channelpointsminer.tests.TestUtils.getAllResourceContent;
import static kong.unirest.core.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiJoinRaidTest{
	public static final String VALID_QUERY = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"c6a332a86d1087fbbb1a8623aa01bd1313d2386e7c63be60fdb2d1901f01a4ae\",\"version\":1}},\"operationName\":\"JoinRaid\",\"variables\":{\"input\":{\"raidID\":\"%s\"}}}";
	private static final String ACCESS_TOKEN = "access-token";
	private static final String RAID_ID = "raid-id";
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void nominalFollowRaid(MockClient unirest){
		var expected = GQLResponse.<JoinRaidData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 4,
						"operationName", "JoinRaid",
						"requestID", "request-id"
				))
				.data(JoinRaidData.builder()
						.joinRaid(JoinRaidPayload.builder()
								.raidId("raid-id")
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(RAID_ID))
				.thenReturn(getAllResourceContent("api/gql/joinRaid.json"))
				.withStatus(200);
		
		assertThat(tested.joinRaid(RAID_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(RAID_ID))
				.thenReturn(getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.joinRaid(RAID_ID));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(RAID_ID))
				.thenReturn(getAllResourceContent("api/gql/invalidRequest.json"))
				.withStatus(200);
		
		assertThat(tested.joinRaid(RAID_ID)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(RAID_ID))
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.joinRaid(RAID_ID)).isEmpty();
		
		unirest.verifyAll();
	}
}