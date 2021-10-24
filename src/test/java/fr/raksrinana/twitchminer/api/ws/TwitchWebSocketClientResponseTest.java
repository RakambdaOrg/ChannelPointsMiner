package fr.raksrinana.twitchminer.api.ws;

import fr.raksrinana.twitchminer.api.ws.data.response.ResponseResponse;
import fr.raksrinana.twitchminer.tests.WebsocketMockServer;
import fr.raksrinana.twitchminer.tests.WebsocketMockServerExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import static fr.raksrinana.twitchminer.tests.TestUtils.getAllResourceContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchWebSocketClientResponseTest{
	private static final int MESSAGE_TIMEOUT = 15000;
	private TwitchWebSocketClient tested;
	
	@Mock
	private TwitchWebSocketListener listener;
	
	@BeforeEach
	void setUp(){
		var uri = URI.create("ws://127.0.0.1:" + WebsocketMockServerExtension.PORT);
		tested = new TwitchWebSocketClient(uri);
		tested.addListener(listener);
	}
	
	@AfterEach
	void tearDown() throws InterruptedException{
		if(tested.isOpen()){
			tested.closeBlocking();
		}
	}
	
	@Test
	void onResponseBadAuthClosesConnection(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
		tested.onMessage(getAllResourceContent("api/ws/response_bad_auth.json"));
		
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketClosed(eq(tested), anyInt(), anyString(), anyBoolean());
		assertThat(server.isReceivedClose()).isTrue();
	}
	
	@Test
	void onResponse(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
		server.send(getAllResourceContent("api/ws/response_ok.json"));
		
		var expected = ResponseResponse.builder()
				.error("")
				.nonce("nonce")
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
}