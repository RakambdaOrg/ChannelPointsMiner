package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.DropProgress;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.dropprogress.DropProgressData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.runnable.SyncInventory;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class ClaimDropHandlerTest{
	@InjectMocks
	private ClaimDropHandler tested;
	
	@Mock
	private SyncInventory syncInventory;
	@Mock
	private Topic topic;
	@Mock
	private DropProgress dropProgress;
	@Mock
	private DropProgressData dropProgressData;
	
	@BeforeEach
	void setUp(){
		lenient().when(dropProgress.getData()).thenReturn(dropProgressData);
	}
	
	@Test
	void dropNotProgressedEnough(){
		when(dropProgressData.getCurrentProgressMin()).thenReturn(1);
		when(dropProgressData.getRequiredProgressMin()).thenReturn(2);
		
		tested.onDropProgress(topic, dropProgress);
		
		verify(syncInventory, never()).run();
	}
	
	@Test
	void dropProgressedEnough(){
		when(dropProgressData.getCurrentProgressMin()).thenReturn(2);
		when(dropProgressData.getRequiredProgressMin()).thenReturn(2);
		
		tested.onDropProgress(topic, dropProgress);
		
		verify(syncInventory).run();
	}
}