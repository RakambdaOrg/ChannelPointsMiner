package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.rakambda.channelpointsminer.miner.api.hermes.TwitchHermesWebSocketPool;
import fr.rakambda.channelpointsminer.miner.api.pubsub.TwitchPubSubWebSocketPool;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
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
class HermesWebSocketPingTest {
	@InjectMocks
	private HermesWebSocketPing tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private TwitchHermesWebSocketPool pool;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getHermesWebSocketPool()).thenReturn(pool);
	}
	
	@Test
	void pingIsCalled(){
		assertDoesNotThrow(() -> tested.run());
		
		verify(pool).ping();
	}
	
	@Test
	void joinPendingIsCalled(){
		assertDoesNotThrow(() -> tested.run());
		
		verify(pool).listenPendingPubSubTopics();
	}
}