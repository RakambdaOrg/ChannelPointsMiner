package fr.rakambda.channelpointsminer.miner.api.chat;

import fr.rakambda.channelpointsminer.miner.event.impl.ChatMessageEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class TwitchChatEventProducerTest{
	private static final Instant NOW = Instant.parse("2020-05-04T20:41:14.000Z");
	
	private final static String STREAMER_NAME = "channel";
	private final static String ACTOR = "username";
	private final static String MESSAGE = "message";
	private final static String BADGE = "badges=predictions/color,sub";
	
	@InjectMocks
	private TwitchChatEventProducer tested;
	
	@Mock
	private IEventManager eventManager;
	
	@Test
	void onMessage(){
		try(var factory = mockStatic(TimeFactory.class)){
			factory.when(TimeFactory::now).thenReturn(NOW);
			
			assertDoesNotThrow(() -> tested.onChatMessage(STREAMER_NAME, ACTOR, MESSAGE));
			
			var expectedEvent = new ChatMessageEvent(NOW, STREAMER_NAME, ACTOR, MESSAGE, "");
			verify(eventManager).onEvent(expectedEvent);
		}
	}
	
	@Test
	void onMessageWithBadge(){
		try(var factory = mockStatic(TimeFactory.class)){
			factory.when(TimeFactory::now).thenReturn(NOW);
			assertDoesNotThrow(() -> tested.onChatMessage(STREAMER_NAME, ACTOR, MESSAGE, BADGE));
			
			var expectedEvent = new ChatMessageEvent(NOW, STREAMER_NAME, ACTOR, MESSAGE, BADGE);
			verify(eventManager).onEvent(expectedEvent);
		}
	}
}