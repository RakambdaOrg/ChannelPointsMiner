package fr.raksrinana.twitchminer.api.gql;

import fr.raksrinana.twitchminer.TestUtils;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.twitchminer.api.gql.data.types.RequestInfo;
import fr.raksrinana.twitchminer.api.gql.data.types.Stream;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import kong.unirest.MockClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.ZonedDateTime;
import java.util.Map;
import static java.time.ZoneOffset.UTC;
import static kong.unirest.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GQLApiReportMenuItemTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String USERNAME = "username";
	private static final String VALID_QUERY = "{\"operationName\":\"ReportMenuItem\",\"extensions\":{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"8f3628981255345ca5e5453dfd844efffb01d6413a9931498836e6268692a30c\"}},\"variables\":{\"channelLogin\":\"%s\"}}";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	private MockClient unirest;
	
	@BeforeEach
	void setUp(){
		TestUtils.setupUnirest();
		unirest = MockClient.register();
		
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void nominalOffline(){
		var expected = GQLResponse.<ReportMenuItemData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 41,
						"operationName", "ReportMenuItem",
						"requestID", "request-id"
				))
				.data(ReportMenuItemData.builder()
						.requestInfo(RequestInfo.builder()
								.countryCode("US")
								.build())
						.user(User.builder()
								.id("123456789")
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/reportMenuItemOffline.json"))
				.withStatus(200);
		
		assertThat(tested.reportMenuItem(USERNAME)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void nominalOnline(){
		var expected = GQLResponse.<ReportMenuItemData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 41,
						"operationName", "ReportMenuItem",
						"requestID", "request-id"
				))
				.data(ReportMenuItemData.builder()
						.requestInfo(RequestInfo.builder()
								.countryCode("US")
								.build())
						.user(User.builder()
								.id("123456789")
								.stream(Stream.builder()
										.id("123456")
										.createdAt(ZonedDateTime.of(2021, 10, 10, 1, 17, 2, 0, UTC))
										.build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/reportMenuItemOnline.json"))
				.withStatus(200);
		
		var result = tested.reportMenuItem(USERNAME);
		assertThat(result).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.reportMenuItem(USERNAME));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(USERNAME))
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.reportMenuItem(USERNAME)).isEmpty();
		
		unirest.verifyAll();
	}
}