package fr.raksrinana.channelpointsminer.api.ws;

import fr.raksrinana.channelpointsminer.tests.WebsocketMockServer;
import fr.raksrinana.channelpointsminer.tests.WebsocketMockServerExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import static fr.raksrinana.channelpointsminer.tests.TestUtils.getAllResourceContent;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchWebSocketClientReconnectTest{
	private static final int MESSAGE_TIMEOUT = 15000;
	
	private TwitchWebSocketClient tested;
	
	@Mock
	private ITwitchWebSocketListener listener;
	
	@AfterEach
	void tearDown(WebsocketMockServer server){
		tested.close();
		server.removeClients();
	}
	
	@Test
	void onReconnectClosesClient() throws InterruptedException{
		tested.connectBlocking();
		
		tested.onMessage(getAllResourceContent("api/ws/reconnect.json"));
		
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketClosed(eq(tested), eq(1001), anyString(), anyBoolean());
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server){
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchWebSocketClient(uri);
		tested.setReuseAddr(true);
		tested.addListener(listener);
	}
}