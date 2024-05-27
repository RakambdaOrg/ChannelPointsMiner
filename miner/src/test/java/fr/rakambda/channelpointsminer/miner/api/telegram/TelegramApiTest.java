package fr.rakambda.channelpointsminer.miner.api.telegram;

import fr.rakambda.channelpointsminer.miner.api.telegram.data.Message;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import fr.rakambda.channelpointsminer.miner.tests.TestUtils;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import kong.unirest.core.HttpMethod;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static kong.unirest.core.ContentType.APPLICATION_JSON;
import static kong.unirest.core.HeaderNames.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
@ParallelizableTest
class TelegramApiTest{
	private static final String URL = "https://base/sendMessage";
	private static final String CHAT_ID = "@test";
	private static final String PAYLOAD = "{\"chat_id\":\"@test\",\"text\":\"Test message\"}";
	
	private TelegramApi tested;
	
	@BeforeEach
	void setUp(UnirestMock unirestMock){
		unirestMock.getUnirestInstance().config().defaultBaseUrl("https://base");
		tested = new TelegramApi(unirestMock.getUnirestInstance());
	}
	
	@Test
	void nominal(UnirestMock unirest){
		var webhook = Message.builder()
				.chatId(CHAT_ID)
				.text("Test message")
				.build();
		
		unirest.expect(HttpMethod.POST, URL)
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.body(PAYLOAD)
				.thenReturn(TestUtils.getAllResourceContent("api/telegram/ok.json"))
				.withStatus(200);
		
		assertDoesNotThrow(() -> tested.sendMessage(webhook));
		
		unirest.verifyAll();
	}
	
	@Test
	void nominalRetryAfter(UnirestMock unirest){
		var webhook = Message.builder()
				.chatId(CHAT_ID)
				.text("Test message")
				.build();
		
		unirest.expect(HttpMethod.POST, URL)
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.body(PAYLOAD)
				.thenReturn(TestUtils.getAllResourceContent("api/telegram/error.json"))
				.withStatus(200);
		
		assertDoesNotThrow(() -> tested.sendMessage(webhook));
		
		unirest.verifyAll(); //Should test it has been invoked 3 times, how?
	}
	
	@Test
	void error(UnirestMock unirest){
		var webhook = Message.builder()
				.chatId(CHAT_ID)
				.text("Test message")
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