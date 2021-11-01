package fr.raksrinana.twitchminer.runnable;

import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.twitchminer.miner.IMiner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebSocketPingTest{
	@InjectMocks
	private WebSocketPing tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private TwitchWebSocketPool pool;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getWebSocketPool()).thenReturn(pool);
	}
	
	@Test
	void pingIsCalled(){
		assertDoesNotThrow(() -> tested.run());
		
		verify(pool).ping();
	}
}