package fr.raksrinana.channelpointsminer.miner.irc;

import fr.raksrinana.channelpointsminer.miner.irc.listeners.TwitchIrcConnectionEventListener;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.kitteh.irc.client.library.event.connection.ClientConnectionClosedEvent;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class TwitchIrcConnectionEventListenerTest{
	private static final String USERNAME = "username";
	
	private final TwitchIrcConnectionEventListener tested = new TwitchIrcConnectionEventListener(USERNAME);
	
	@Mock
	private ClientConnectionClosedEvent clientConnectionClosedEvent;
	
	@Test
	void reconnectIfAttemptCanBeMade(){
		when(clientConnectionClosedEvent.canAttemptReconnect()).thenReturn(true);
		
		assertDoesNotThrow(() -> tested.onClientConnectionCLoseEvent(clientConnectionClosedEvent));
		
		verify(clientConnectionClosedEvent).setAttemptReconnect(true);
	}
	
	@Test
	void cannotReconnect(){
		when(clientConnectionClosedEvent.canAttemptReconnect()).thenReturn(false);
		
		assertDoesNotThrow(() -> tested.onClientConnectionCLoseEvent(clientConnectionClosedEvent));
		
		verify(clientConnectionClosedEvent, never()).setAttemptReconnect(anyBoolean());
	}
}