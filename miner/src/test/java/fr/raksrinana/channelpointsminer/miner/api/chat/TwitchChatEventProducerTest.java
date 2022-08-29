package fr.raksrinana.channelpointsminer.miner.api.chat;

import fr.raksrinana.channelpointsminer.miner.event.impl.ChatMessageEvent;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
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
	private IMiner miner;
	
	@Test
	void onMessage(){
		try(var factory = mockStatic(TimeFactory.class)){
			factory.when(TimeFactory::now).thenReturn(NOW);
			
			assertDoesNotThrow(() -> tested.onChatMessage(STREAMER_NAME, ACTOR, MESSAGE));
			
			var expectedEvent = new ChatMessageEvent(miner, NOW, STREAMER_NAME, ACTOR, MESSAGE, "");
			verify(miner).onEvent(expectedEvent);
		}
	}
	
	@Test
	void onMessageWithBadge(){
		try(var factory = mockStatic(TimeFactory.class)){
			factory.when(TimeFactory::now).thenReturn(NOW);
			assertDoesNotThrow(() -> tested.onChatMessage(STREAMER_NAME, ACTOR, MESSAGE, BADGE));
			
			var expectedEvent = new ChatMessageEvent(miner, NOW, STREAMER_NAME, ACTOR, MESSAGE, BADGE);
			verify(miner).onEvent(expectedEvent);
		}
	}
}