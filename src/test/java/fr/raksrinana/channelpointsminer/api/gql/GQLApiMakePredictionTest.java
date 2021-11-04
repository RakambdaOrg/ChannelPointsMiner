package fr.raksrinana.channelpointsminer.api.gql;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.makeprediction.MakePredictionData;
import fr.raksrinana.channelpointsminer.api.gql.data.types.MakePredictionError;
import fr.raksrinana.channelpointsminer.api.gql.data.types.MakePredictionErrorCode;
import fr.raksrinana.channelpointsminer.api.gql.data.types.MakePredictionPayload;
import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.tests.UnirestMockExtension;
import kong.unirest.MockClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static fr.raksrinana.channelpointsminer.tests.TestUtils.getAllResourceContent;
import static kong.unirest.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class GQLApiMakePredictionTest{
	public static final String VALID_QUERY = "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"b44682ecc88358817009f20e69d75081b1e58825bb40aa53d5dbadcc17c881d8\",\"version\":1}},\"operationName\":\"MakePrediction\",\"variables\":{\"input\":{\"eventID\":\"%s\",\"outcomeID\":\"%s\",\"points\":%d,\"transactionID\":\"%s\"}}}";
	private static final String ACCESS_TOKEN = "access-token";
	private static final String EVENT_ID = "event-id";
	private static final String OUTCOME_ID = "outcome-id";
	private static final int POINTS = 20;
	private static final String TRANSACTION_ID = "transaction-id";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void nominalMakePrediction(MockClient unirest){
		var expected = GQLResponse.<MakePredictionData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 127,
						"operationName", "MakePrediction",
						"requestID", "request-id"
				))
				.data(MakePredictionData.builder()
						.makePrediction(MakePredictionPayload.builder().build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(EVENT_ID, OUTCOME_ID, POINTS, TRANSACTION_ID))
				.thenReturn(getAllResourceContent("api/gql/makePrediction_success.json"))
				.withStatus(200);
		
		assertThat(tested.makePrediction(EVENT_ID, OUTCOME_ID, POINTS, TRANSACTION_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void errorMakePrediction(MockClient unirest){
		var expected = GQLResponse.<MakePredictionData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 37,
						"operationName", "MakePrediction",
						"requestID", "request-id"
				))
				.data(MakePredictionData.builder()
						.makePrediction(MakePredictionPayload.builder()
								.error(MakePredictionError.builder()
										.code(MakePredictionErrorCode.NOT_ENOUGH_POINTS)
										.build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(EVENT_ID, OUTCOME_ID, POINTS, TRANSACTION_ID))
				.thenReturn(getAllResourceContent("api/gql/makePrediction_notEnoughPoints.json"))
				.withStatus(200);
		
		assertThat(tested.makePrediction(EVENT_ID, OUTCOME_ID, POINTS, TRANSACTION_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(EVENT_ID, OUTCOME_ID, POINTS, TRANSACTION_ID))
				.thenReturn(getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.makePrediction(EVENT_ID, OUTCOME_ID, POINTS, TRANSACTION_ID));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(EVENT_ID, OUTCOME_ID, POINTS, TRANSACTION_ID))
				.thenReturn(getAllResourceContent("api/gql/invalidRequest.json"))
				.withStatus(200);
		
		assertThat(tested.makePrediction(EVENT_ID, OUTCOME_ID, POINTS, TRANSACTION_ID)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(MockClient unirest){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(EVENT_ID, OUTCOME_ID, POINTS, TRANSACTION_ID))
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.makePrediction(EVENT_ID, OUTCOME_ID, POINTS, TRANSACTION_ID)).isEmpty();
		
		unirest.verifyAll();
	}
}