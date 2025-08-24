package fr.rakambda.channelpointsminer.miner.api.pubsub;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.response.ResponseResponse;
import fr.rakambda.channelpointsminer.miner.tests.TestUtils;
import fr.rakambda.channelpointsminer.miner.tests.WebsocketMockServer;
import fr.rakambda.channelpointsminer.miner.tests.WebsocketMockServerExtension;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.awaitility.core.ConditionTimeoutException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchPubSubWebSocketClientResponseTest{
	private static final int MESSAGE_TIMEOUT = 15000;
	private TwitchPubSubWebSocketClient tested;
	
	@Mock
	private ITwitchPubSubWebSocketListener listener;
	
	@AfterEach
	void tearDown(WebsocketMockServer server){
		tested.close();
		server.removeClients();
	}
	
	@Test
	void onResponseBadAuthClosesConnection(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage();
		
		tested.onMessage(TestUtils.getAllResourceContent("api/ws/response_badAuth.json"));
		
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketClosed(eq(tested), anyInt(), anyString(), anyBoolean());
		assertThat(server.isReceivedClose()).isTrue();
	}
	
	@RepeatedIfExceptionsTest(repeats = 5, exceptions = ConditionTimeoutException.class)
	void onResponse(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage();
		
		server.send(TestUtils.getAllResourceContent("api/ws/response_ok.json"));
		
		var expected = ResponseResponse.builder()
				.error("")
				.nonce("nonce")
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server){
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchPubSubWebSocketClient(uri);
		tested.setReuseAddr(true);
		tested.addListener(listener);
	}
}