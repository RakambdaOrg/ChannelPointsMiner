package fr.raksrinana.channelpointsminer.api.discord;

import fr.raksrinana.channelpointsminer.api.discord.data.Webhook;
import fr.raksrinana.channelpointsminer.tests.TestUtils;
import fr.raksrinana.channelpointsminer.tests.UnirestMockExtension;
import kong.unirest.HttpMethod;
import kong.unirest.MockClient;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import static kong.unirest.ContentType.APPLICATION_JSON;
import static kong.unirest.HeaderNames.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class DiscordApiTest{
	private static final String URL = "https://webhook";
	private static final String PAYLOAD = "{\"content\":\"Test message\",\"username\":\"ChannelPointsMiner\"}";
	
	private DiscordApi tested;
	
	@BeforeEach
	void setUp() throws MalformedURLException{
		var url = new URL(URL);
		tested = new DiscordApi(url);
	}
	
	@Test
	void nominal(MockClient unirest){
		var webhook = Webhook.builder()
				.username("UsernameWillBeOverriden")
				.content("Test message")
				.build();
		
		unirest.expect(HttpMethod.POST, URL)
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.body(PAYLOAD)
				.thenReturn()
				.withStatus(204);
		
		assertDoesNotThrow(() -> tested.sendMessage(webhook));
		
		unirest.verifyAll();
	}
	
	@Test
	void nominalRetryAfter(MockClient unirest){
		var webhook = Webhook.builder()
				.content("Test message")
				.build();
		
		unirest.expect(HttpMethod.POST, URL)
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.body(PAYLOAD)
				.thenReturn(TestUtils.getAllResourceContent("api/discord/retryAfter.json"))
				.withStatus(429);
		
		assertDoesNotThrow(() -> tested.sendMessage(webhook));
		
		unirest.verifyAll(); //Should test it has been invoked 3 times, how?
	}
	
	@Test
	void error(MockClient unirest){
		var webhook = Webhook.builder()
				.content("Test message")
				.build();
		
		unirest.expect(HttpMethod.POST, URL)
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.body(PAYLOAD)
				.thenReturn()
				.withStatus(500);
		
		assertDoesNotThrow(() -> tested.sendMessage(webhook));
		
		unirest.verifyAll();
	}
}