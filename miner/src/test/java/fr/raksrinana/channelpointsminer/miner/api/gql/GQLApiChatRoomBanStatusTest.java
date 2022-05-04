package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.chatroombanstatus.ChatRoomBanStatusData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.ChatRoomBanStatus;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.User;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.TestUtils;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import kong.unirest.core.MockClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.ZonedDateTime;
import java.util.Map;
import static java.time.ZoneOffset.UTC;
import static kong.unirest.core.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiChatRoomBanStatusTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String USERNAME = "username";
	private static final String CHANNEL = "channel";
	private static final String VALID_QUERY = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"319f2a9a3ac7ddecd7925944416c14b818b65676ab69da604460b68938d22bea\",\"version\":1}},\"operationName\":\"ChatRoomBanStatus\",\"variables\":{\"targetUserID\":\"%s\",\"channelID\":\"%s\"}}";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void notBanned(MockClient unirest){
		var expected = GQLResponse.<ChatRoomBanStatusData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 21,
						"operationName", "ChatRoomBanStatus",
						"requestID", "request-id"
				))
				.data(ChatRoomBanStatusData.builder()
						.targetUser(User.builder()
								.id("123456789")
								.login("target-username")
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME, CHANNEL))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/channelRoomBanStatus_notBanned.json"))
				.withStatus(200);
		
		assertThat(tested.chatRoomBanStatus(CHANNEL, USERNAME)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void banned(MockClient unirest){
		var expected = GQLResponse.<ChatRoomBanStatusData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 21,
						"operationName", "ChatRoomBanStatus",
						"requestID", "request-id"
				))
				.data(ChatRoomBanStatusData.builder()
						.chatRoomBanStatus(ChatRoomBanStatus.builder()
								.bannedUser(User.builder()
										.id("123456789")
										.login("target-username")
										.displayName("target-name")
										.build())
								.createdAt(ZonedDateTime.of(2022, 1, 1, 10, 22, 35, 0, UTC))
								.permanent(true)
								.reason("")
								.build())
						.targetUser(User.builder()
								.id("123456789")
								.login("target-username")
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME, CHANNEL))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/channelRoomBanStatus_banned.json"))
				.withStatus(200);
		
		var actual = tested.chatRoomBanStatus(CHANNEL, USERNAME);
		assertThat(actual).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME, CHANNEL))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.chatRoomBanStatus(CHANNEL, USERNAME));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME, CHANNEL))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidRequest.json"))
				.withStatus(200);
		
		assertThat(tested.chatRoomBanStatus(CHANNEL, USERNAME)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME, CHANNEL))
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.chatRoomBanStatus(CHANNEL, USERNAME)).isEmpty();
		
		unirest.verifyAll();
	}
}