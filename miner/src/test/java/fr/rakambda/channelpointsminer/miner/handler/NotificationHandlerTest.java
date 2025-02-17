package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.CreateNotification;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.createnotification.CreateNotificationData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.createnotification.Notification;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class NotificationHandlerTest{
	@InjectMocks
	private NotificationHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private CreateNotification createNotification;
	@Mock
	private CreateNotificationData createNotificationData;
	@Mock
	private Notification notification;
	@Mock
	private Topic topic;
	
	@Test
	void syncInventoryOnDropNotification(){
		when(createNotification.getData()).thenReturn(createNotificationData);
		when(createNotificationData.getNotification()).thenReturn(notification);
		when(notification.getType()).thenReturn("user_drop_reward_reminder_notification");
		
		assertDoesNotThrow(() -> tested.handle(topic, createNotification));
		
		verify(miner).syncInventory();
	}
	
	@Test
	void doesNotSyncInventoryOnOtherNotification(){
		when(createNotification.getData()).thenReturn(createNotificationData);
		when(createNotificationData.getNotification()).thenReturn(notification);
		when(notification.getType()).thenReturn("other_notification");
		
		assertDoesNotThrow(() -> tested.handle(topic, createNotification));
		
		verify(miner, never()).syncInventory();
	}
}