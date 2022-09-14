package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.chatroombanstatus.ChatRoomBanStatusData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.ChatRoomBanStatus;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.User;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMock;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.ZonedDateTime;
import java.util.Map;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiChatRoomBanStatusTest extends AbstractGQLTest{
	private static final String USERNAME = "username";
	private static final String CHANNEL = "channel";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void notBanned(UnirestMock unirest){
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
		
		expectValidRequestOkWithIntegrityOk(unirest, "api/gql/channelRoomBanStatus_notBanned.json");
		
		assertThat(tested.chatRoomBanStatus(CHANNEL, USERNAME)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void banned(UnirestMock unirest){
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
		
		expectValidRequestOkWithIntegrityOk(unirest, "api/gql/channelRoomBanStatus_banned.json");
		
		var actual = tested.chatRoomBanStatus(CHANNEL, USERNAME);
		assertThat(actual).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"319f2a9a3ac7ddecd7925944416c14b818b65676ab69da604460b68938d22bea\",\"version\":1}},\"operationName\":\"ChatRoomBanStatus\",\"variables\":{\"targetUserID\":\"%s\",\"channelID\":\"%s\"}}".formatted(USERNAME, CHANNEL);
	}
}