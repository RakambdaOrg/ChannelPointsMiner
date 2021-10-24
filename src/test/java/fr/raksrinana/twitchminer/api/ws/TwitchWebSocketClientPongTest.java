package fr.raksrinana.twitchminer.api.ws;

import fr.raksrinana.twitchminer.api.ws.data.response.PongResponse;
import fr.raksrinana.twitchminer.factory.TimeFactory;
import fr.raksrinana.twitchminer.tests.WebsocketMockServer;
import fr.raksrinana.twitchminer.tests.WebsocketMockServerExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.time.Instant;
import static fr.raksrinana.twitchminer.tests.TestUtils.getAllResourceContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
@Disabled("Doesn't pass on CI")
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
		
		server.send(getAllResourceContent("api/ws/pong.json"));
		
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(any(PongResponse.class));
	}
}