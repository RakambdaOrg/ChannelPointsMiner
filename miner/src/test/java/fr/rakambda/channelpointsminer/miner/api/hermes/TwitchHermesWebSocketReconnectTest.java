package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.ReconnectResponse;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
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
import static org.java_websocket.framing.CloseFrame.GOING_AWAY;
import static org.java_websocket.framing.CloseFrame.NORMAL;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchHermesWebSocketReconnectTest{
	private static final int MESSAGE_TIMEOUT = 15000;
	private TwitchHermesWebSocketClient tested;
	
	@Mock
	private ITwitchHermesWebSocketListener listener;
	@Mock
	private IEventManager eventManager;
	
	@AfterEach
	void tearDown(WebsocketMockServer server){
		tested.close();
		server.removeClients();
	}
	
	@RepeatedIfExceptionsTest(repeats = 5, exceptions = ConditionTimeoutException.class)
	void onResponse(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
		server.send(TestUtils.getAllResourceContent("api/hermes/reconnect_ok.json"));
		
		var expected = ReconnectResponse.builder()
				.id("4ae4865b-cedc-4755-8335-0560b7d05341")
				.timestamp(ZonedDateTime.parse("2025-01-02T03:04:05.123456789Z"))
				.reconnect(ReconnectResponse.Reconnect.builder().build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
		verify(listener).onWebSocketClosed(eq(tested), eq(GOING_AWAY), anyString(), anyBoolean());
	}
	
	@RepeatedIfExceptionsTest(repeats = 5, exceptions = ConditionTimeoutException.class)
	void onResponseWithReconnectUrl(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
		server.send(TestUtils.getAllResourceContent("api/hermes/reconnect_ok_url.json"));
		
		var expected = ReconnectResponse.builder()
				.id("4ae4865b-cedc-4755-8335-0560b7d05341")
				.timestamp(ZonedDateTime.parse("2025-01-02T03:04:05.123456789Z"))
				.reconnect(ReconnectResponse.Reconnect.builder()
						.url("wss://test")
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
		verify(listener).onWebSocketClosed(eq(tested), eq(GOING_AWAY), anyString(), anyBoolean());
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server){
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchHermesWebSocketClient(uri, eventManager);
		tested.setReuseAddr(true);
		tested.addListener(listener);
	}
}