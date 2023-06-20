package fr.rakambda.channelpointsminer.miner.event.manager;

import fr.rakambda.channelpointsminer.miner.event.IEvent;
import fr.rakambda.channelpointsminer.miner.event.IEventHandler;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class EventManagerTest{
	@InjectMocks
	private EventManager tested;
	
	@Mock
	private ExecutorService executorService;
	
	@BeforeEach
	void setUp(){
		lenient().when(executorService.submit(any(Runnable.class))).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return CompletableFuture.completedFuture(null);
		});
	}
	
	@Test
	void eventHandlers(){
		var handler1 = mock(IEventHandler.class);
		var handler2 = mock(IEventHandler.class);
		
		tested.addEventHandler(handler1);
		tested.addEventHandler(handler2);
		
		var event = mock(IEvent.class);
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(executorService, times(2)).submit(any(Runnable.class));
		verify(handler1).onEvent(event);
		verify(handler2).onEvent(event);
	}
}