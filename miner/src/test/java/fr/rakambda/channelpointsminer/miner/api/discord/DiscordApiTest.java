package fr.rakambda.channelpointsminer.miner.api.discord;

import fr.rakambda.channelpointsminer.miner.api.discord.data.Webhook;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import fr.rakambda.channelpointsminer.miner.tests.TestUtils;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import kong.unirest.core.HttpMethod;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import static kong.unirest.core.ContentType.APPLICATION_JSON;
import static kong.unirest.core.HeaderNames.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
@ParallelizableTest
class DiscordApiTest{
	private static final String URL = "https://webhook";
	private static final String PAYLOAD = "{\"content\":\"Test message\",\"username\":\"ChannelPointsMiner\"}";
	
	private DiscordApi tested;
	
	@BeforeEach
	void setUp(UnirestMock unirestMock) throws MalformedURLException{
		var url = URI.create(URL).toURL();
		tested = new DiscordApi(url, unirestMock.getUnirestInstance());
	}
	
	@Test
	void nominal(UnirestMock unirest){
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
	void nominalRetryAfter(UnirestMock unirest){
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
	void error(UnirestMock unirest){
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