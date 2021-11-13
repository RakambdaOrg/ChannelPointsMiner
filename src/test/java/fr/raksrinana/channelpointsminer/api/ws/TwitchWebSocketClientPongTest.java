package fr.raksrinana.channelpointsminer.api.ws;

import fr.raksrinana.channelpointsminer.api.ws.data.response.PongResponse;
import fr.raksrinana.channelpointsminer.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.tests.WebsocketMockServer;
import fr.raksrinana.channelpointsminer.tests.WebsocketMockServerExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.channelpointsminer.tests.TestUtils.getAllResourceContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
// @EnabledIfEnvironmentVariable(named = "EXECUTE_DISABLED_CI", matches = ".*", disabledReason = "Doesn't pass on CI")
class TwitchWebSocketClientPongTest{
	private static final Instant NOW = Instant.parse("2021-02-25T15:25:36Z");
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
		await().atMost(10, TimeUnit.SECONDS).until(() -> !server.getReceivedMessages().isEmpty());
		
		server.send(getAllResourceContent("api/ws/pong.json"));
		
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(any(PongResponse.class));
	}
}