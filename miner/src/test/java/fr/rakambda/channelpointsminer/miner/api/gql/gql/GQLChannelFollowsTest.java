package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.channelfollows.ChannelFollowsData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.FollowConnection;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.FollowEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.PageInfo;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLChannelFollowsTest extends AbstractGQLTest{
	public static final String VALID_QUERY = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"eecf815273d3d949e5cf0085cc5084cd8a1b5b7b6f7990cf43cb0beadf546907\",\"version\":1}},\"operationName\":\"ChannelFollows\",\"variables\":{\"limit\":%d,\"order\":\"%s\"}}";
	public static final String VALID_QUERY_WITH_CURSOR = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"eecf815273d3d949e5cf0085cc5084cd8a1b5b7b6f7990cf43cb0beadf546907\",\"version\":1}},\"operationName\":\"ChannelFollows\",\"variables\":{\"cursor\":\"%s\",\"limit\":%d,\"order\":\"%s\"}}";
	private static final int LIMIT = 15;
	private static final int ALL_LIMIT = 100;
	private static final String ORDER = "DESC";
	
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
														.login("login")
														.profileImageUrl(new URL("https://profile-image"))
														.build())
												.build()))
										.pageInfo(PageInfo.builder()
												.hasNextPage(false)
												.build())
										.build())
								.build())
						.build())
				.build();
		expectValidRequestOkWithIntegrityOk("api/gql/gql/channelFollows_oneFollow.json");
		
		assertThat(tested.channelFollows(LIMIT, ORDER, null)).contains(expected);
		
		verifyAll();
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
														.login("login")
														.profileImageUrl(new URL("https://profile-image"))
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
		
		expectBodyRequestOkWithIntegrityOk(VALID_QUERY_WITH_CURSOR.formatted(cursor, LIMIT, ORDER), "api/gql/gql/channelFollows_oneFollow.json");
		
		assertThat(tested.channelFollows(LIMIT, ORDER, cursor)).contains(expected);
		
		verifyAll();
	}
	
	@Test
	void getAllFollowsNominal(){
		expectBodyRequestOkWithIntegrityOk(VALID_QUERY.formatted(ALL_LIMIT, ORDER), "api/gql/gql/channelFollows_severalFollows.json");
		expectBodyRequestOk(VALID_QUERY_WITH_CURSOR.formatted("cursor-id-2", ALL_LIMIT, ORDER), "api/gql/gql/channelFollows_oneFollow.json");
		
		assertThat(tested.allChannelFollows()).hasSize(3);
		
		verifyAll();
	}
	
	@Test
	void getAllFollowsNominalOnePage(){
		expectBodyRequestOkWithIntegrityOk(VALID_QUERY.formatted(ALL_LIMIT, ORDER), "api/gql/gql/channelFollows_oneFollow.json");
		
		assertThat(tested.allChannelFollows()).hasSize(1);
		
		verifyAll();
	}
	
	@Test
	void getAllFollowsErrorGettingCursor(){
		expectBodyRequestOkWithIntegrityOk(VALID_QUERY.formatted(ALL_LIMIT, ORDER), "api/gql/gql/channelFollows_invalidCursor.json");
		
		assertThrows(IllegalStateException.class, () -> tested.allChannelFollows());
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return VALID_QUERY.formatted(LIMIT, ORDER);
	}
}