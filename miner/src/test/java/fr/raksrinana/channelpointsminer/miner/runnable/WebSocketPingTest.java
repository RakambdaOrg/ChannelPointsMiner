package fr.raksrinana.channelpointsminer.miner.runnable;

import fr.raksrinana.channelpointsminer.miner.api.ws.TwitchWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ParallelizableTest
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