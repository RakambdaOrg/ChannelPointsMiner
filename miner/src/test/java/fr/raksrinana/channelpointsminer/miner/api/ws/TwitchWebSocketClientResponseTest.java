package fr.raksrinana.channelpointsminer.miner.api.ws;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.response.ResponseResponse;
import fr.raksrinana.channelpointsminer.miner.tests.WebsocketMockServer;
import fr.raksrinana.channelpointsminer.miner.tests.WebsocketMockServerExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import static fr.raksrinana.channelpointsminer.miner.tests.TestUtils.getAllResourceContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchWebSocketClientResponseTest{
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
	void onResponseBadAuthClosesConnection(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage();
		
		tested.onMessage(getAllResourceContent("api/ws/response_badAuth.json"));
		
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketClosed(eq(tested), anyInt(), anyString(), anyBoolean());
		assertThat(server.isReceivedClose()).isTrue();
	}
	
	@Test
	void onResponse(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage();
		
		server.send(getAllResourceContent("api/ws/response_ok.json"));
		
		var expected = ResponseResponse.builder()
				.error("")
				.nonce("nonce")
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server){
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchWebSocketClient(uri);
		tested.setReuseAddr(true);
		tested.addListener(listener);
	}
}