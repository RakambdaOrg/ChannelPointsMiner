package fr.raksrinana.channelpointsminer.miner.api.ws;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.response.PongResponse;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.tests.WebsocketMockServer;
import fr.raksrinana.channelpointsminer.miner.tests.WebsocketMockServerExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.time.Instant;
import static fr.raksrinana.channelpointsminer.miner.tests.TestUtils.getAllResourceContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchPubSubWebSocketClientPongTest{
	private static final Instant NOW = Instant.parse("2021-02-25T15:25:36Z");
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
	void onPongUpdatesLastPong(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			tested.onMessage(getAllResourceContent("api/ws/pong.json"));
			
			assertThat(tested.getLastPong()).isEqualTo(NOW);
		}
	}
	
	@Test
	void onPong(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage();
		
		server.send(getAllResourceContent("api/ws/pong.json"));
		
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(any(PongResponse.class));
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server){
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchPubSubWebSocketClient(uri);
		tested.setReuseAddr(true);
		tested.addListener(listener);
	}
}