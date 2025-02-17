package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatClient;
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
class WebSocketPingTest{
	@InjectMocks
	private WebSocketPing tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private TwitchPubSubWebSocketPool pool;
	@Mock
	private ITwitchChatClient chat;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getPubSubWebSocketPool()).thenReturn(pool);
		lenient().when(miner.getChatClient()).thenReturn(chat);
	}
	
	@Test
	void pingIsCalled(){
		assertDoesNotThrow(() -> tested.run());
		
		verify(pool).ping();
		verify(chat).ping();
	}
	
	@Test
	void joinPendingIsCalled(){
		assertDoesNotThrow(() -> tested.run());
		
		verify(pool).listenPendingTopics();
		verify(chat).joinPending();
	}
}