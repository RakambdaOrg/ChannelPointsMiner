package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.channelfollows.ChannelFollowsData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.FollowConnection;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.FollowEdge;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.FollowerEdge;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.PageInfo;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.User;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.UserSelfConnection;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMock;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLChannelFollowsTest extends AbstractGQLTest{
	public static final String VALID_QUERY = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"4b9cb31b54b9213e5760f2f6e9e935ad09924cac2f78aac51f8a64d85f028ed0\",\"version\":1}},\"operationName\":\"ChannelFollows\",\"variables\":{\"limit\":%d,\"order\":\"%s\"}}";
	public static final String VALID_QUERY_WITH_CURSOR = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"4b9cb31b54b9213e5760f2f6e9e935ad09924cac2f78aac51f8a64d85f028ed0\",\"version\":1}},\"operationName\":\"ChannelFollows\",\"variables\":{\"cursor\":\"%s\",\"limit\":%d,\"order\":\"%s\"}}";
	private static final int LIMIT = 15;
	private static final int ALL_LIMIT = 100;
	private static final String ORDER = "DESC";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void nominal(UnirestMock unirest) throws MalformedURLException{
		var expected = GQLResponse.<ChannelFollowsData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 102,
						"operationName", "ChannelFollows",
						"requestID", "request-id"
				))
				.data(ChannelFollowsData.builder()
						.user(User.builder()
								.id("123456789")
								.follows(FollowConnection.builder()
										.edges(List.of(FollowEdge.builder()
												.cursor("cursor-id")
												.node(User.builder()
														.id("987654321")
														.displayName("display-name")
														.login("login")
														.profileImageUrl(new URL("https://profile-image"))
														.self(UserSelfConnection.builder()
																.canFollow(true)
																.follower(FollowerEdge.builder()
																		.disableNotifications(true)
																		.followedAt(ZonedDateTime.of(2021, 9, 19, 16, 14, 15, 0, UTC))
																		.build())
																.build())
														.build())
												.build()))
										.pageInfo(PageInfo.builder()
												.hasNextPage(false)
												.build())
										.build())
								.build())
						.build())
				.build();
		expectValidRequestOkWithIntegrityOk(unirest, "api/gql/channelFollows_oneFollow.json");
		
		assertThat(tested.channelFollows(LIMIT, ORDER, null)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void nominalWithCursor(UnirestMock unirest) throws MalformedURLException{
		var expected = GQLResponse.<ChannelFollowsData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 102,
						"operationName", "ChannelFollows",
						"requestID", "request-id"
				))
				.data(ChannelFollowsData.builder()
						.user(User.builder()
								.id("123456789")
								.follows(FollowConnection.builder()
										.edges(List.of(FollowEdge.builder()
												.cursor("cursor-id")
												.node(User.builder()
														.id("987654321")
														.displayName("display-name")
														.login("login")
														.profileImageUrl(new URL("https://profile-image"))
														.self(UserSelfConnection.builder()
																.canFollow(true)
																.follower(FollowerEdge.builder()
																		.disableNotifications(true)
																		.followedAt(ZonedDateTime.of(2021, 9, 19, 16, 14, 15, 0, UTC))
																		.build())
																.build())
														.build())
												.build()))
										.pageInfo(PageInfo.builder()
												.hasNextPage(false)
												.build())
										.build())
								.build())
						.build())
				.build();
		
		var cursor = "my-cursor";
		
		expectBodyRequestOkWithIntegrityOk(unirest, VALID_QUERY_WITH_CURSOR.formatted(cursor, LIMIT, ORDER), "api/gql/channelFollows_oneFollow.json");
		
		assertThat(tested.channelFollows(LIMIT, ORDER, cursor)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void getAllFollowsNominal(UnirestMock unirest){
		setupIntegrityOk(unirest);
		expectBodyRequestOk(unirest, VALID_QUERY.formatted(ALL_LIMIT, ORDER), "api/gql/channelFollows_severalFollows.json");
		expectBodyRequestOk(unirest, VALID_QUERY_WITH_CURSOR.formatted("cursor-id-2", ALL_LIMIT, ORDER), "api/gql/channelFollows_oneFollow.json");
		
		assertThat(tested.allChannelFollows()).hasSize(3);
		
		unirest.verifyAll();
	}
	
	@Test
	void getAllFollowsNominalOnePage(UnirestMock unirest){
		expectBodyRequestOkWithIntegrityOk(unirest, VALID_QUERY.formatted(ALL_LIMIT, ORDER), "api/gql/channelFollows_oneFollow.json");
		
		assertThat(tested.allChannelFollows()).hasSize(1);
		
		unirest.verifyAll();
	}
	
	@Test
	void getAllFollowsErrorGettingCursor(UnirestMock unirest){
		expectBodyRequestOkWithIntegrityOk(unirest, VALID_QUERY.formatted(ALL_LIMIT, ORDER), "api/gql/channelFollows_invalidCursor.json");
		
		assertThrows(IllegalStateException.class, () -> tested.allChannelFollows());
		
		unirest.verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return VALID_QUERY.formatted(LIMIT, ORDER);
	}
}