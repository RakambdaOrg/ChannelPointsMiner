package fr.raksrinana.twitchminer.api.gql;

import fr.raksrinana.twitchminer.TestUtils;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.twitchminer.api.gql.data.types.Channel;
import fr.raksrinana.twitchminer.api.gql.data.types.Inventory;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import kong.unirest.MockClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import static kong.unirest.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GQLApiDropsHighlightServiceAvailableDropsTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String STREAMER_ID = "streamer-id";
	public static final String VALID_QUERY = "{\"operationName\":\"DropsHighlightService_AvailableDrops\",\"extensions\":{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"b19ee96a0e79e3f8281c4108bc4c7b3f232266db6f96fd04a339ab393673a075\"}},\"variables\":{\"channelID\":\"%s\"}}";
	
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
	void nominalNoDrops(){
		var expected = GQLResponse.<DropsHighlightServiceAvailableDropsData> builder()
				.extensions(Map.of(
						"durationMilliseconds", 31,
						"operationName", "DropsHighlightService_AvailableDrops",
						"requestID", "request-id"
				))
				.data(DropsHighlightServiceAvailableDropsData.builder()
						.channel(Channel.builder()
								.id("123456789")
								.build())
						.currentUser(User.builder()
								.id("987654321")
								.inventory(Inventory.builder().build())
								.build())
						.build())
				.build();
		
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(STREAMER_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/dropsHighlightServiceAvailableDrops_noDrops.json"))
				.withStatus(200);
		
		assertThat(tested.dropsHighlightServiceAvailableDrops(STREAMER_ID)).isPresent().get().isEqualTo(expected);
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidCredentials(){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(STREAMER_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidAuth.json"))
				.withStatus(401);
		
		assertThrows(RuntimeException.class, () -> tested.dropsHighlightServiceAvailableDrops(STREAMER_ID));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(STREAMER_ID))
				.thenReturn(TestUtils.getAllResourceContent("api/gql/invalidRequest.json"))
				.withStatus(200);
		
		assertThat(tested.dropsHighlightServiceAvailableDrops(STREAMER_ID)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.body(VALID_QUERY.formatted(STREAMER_ID))
				.thenReturn()
				.withStatus(500);
		
		assertThat(tested.dropsHighlightServiceAvailableDrops(STREAMER_ID)).isEmpty();
		
		unirest.verifyAll();
	}
}