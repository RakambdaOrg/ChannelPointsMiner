package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.RequestInfo;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.Stream;
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
class GQLApiReportMenuItemTest extends AbstractGQLTest{
	private static final String USERNAME = "username";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void nominalOffline(UnirestMock unirest){
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
		
		expectValidRequestOkWithIntegrityOk(unirest, "api/gql/reportMenuItem_offline.json");
		
		assertThat(tested.reportMenuItem(USERNAME)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void nominalOnline(UnirestMock unirest){
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
		
		expectValidRequestOkWithIntegrityOk(unirest, "api/gql/reportMenuItem_online.json");
		
		var result = tested.reportMenuItem(USERNAME);
		assertThat(result).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"8f3628981255345ca5e5453dfd844efffb01d6413a9931498836e6268692a30c\",\"version\":1}},\"operationName\":\"ReportMenuItem\",\"variables\":{\"channelLogin\":\"%s\"}}".formatted(USERNAME);
	}
}