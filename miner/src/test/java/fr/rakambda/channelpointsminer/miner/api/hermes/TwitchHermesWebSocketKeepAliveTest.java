package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.KeepAliveResponse;
import fr.rakambda.channelpointsminer.miner.tests.TestUtils;
import fr.rakambda.channelpointsminer.miner.tests.WebsocketMockServer;
import fr.rakambda.channelpointsminer.miner.tests.WebsocketMockServerExtension;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.awaitility.core.ConditionTimeoutException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.time.ZonedDateTime;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchHermesWebSocketKeepAliveTest {
	private static final int MESSAGE_TIMEOUT = 15000;
	private TwitchHermesWebSocketClient tested;
	
	@Mock
	private ITwitchHermesWebSocketListener listener;
	
	@AfterEach
	void tearDown(WebsocketMockServer server){
		tested.close();
		server.removeClients();
	}
	
	@RepeatedIfExceptionsTest(repeats = 5, exceptions = ConditionTimeoutException.class)
	void onResponse(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
		server.send(TestUtils.getAllResourceContent("api/hermes/keep_alive_ok.json"));
		
		var expected = KeepAliveResponse.builder()
				.id("4ae4865b-cedc-4755-8335-0560b7d05341")
				.timestamp(ZonedDateTime.parse("2025-01-02T03:04:05.123456789Z"))
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server){
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchHermesWebSocketClient(uri);
		tested.setReuseAddr(true);
		tested.addListener(listener);
	}
}