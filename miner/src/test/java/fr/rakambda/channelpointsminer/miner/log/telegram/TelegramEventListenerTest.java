package fr.rakambda.channelpointsminer.miner.log.telegram;

import fr.rakambda.channelpointsminer.miner.api.telegram.TelegramApi;
import fr.rakambda.channelpointsminer.miner.api.telegram.data.Message;
import fr.rakambda.channelpointsminer.miner.config.MessageEventConfiguration;
import fr.rakambda.channelpointsminer.miner.config.TelegramConfiguration;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.IEvent;
import fr.rakambda.channelpointsminer.miner.event.ILoggableEvent;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class TelegramEventListenerTest {
	private static final String USERNAME = "123456789";
	
	@InjectMocks
	private TelegramEventListener tested;
	
	@Mock
	private TelegramApi telegramApi;
	@Mock
	private TelegramConfiguration telegramConfiguration;
	@Mock
	private TelegramMessageBuilder telegramMessageBuilder;
	
	@BeforeEach
	void setUp(){
		lenient().when(telegramConfiguration.getChatId()).thenReturn(USERNAME);
		lenient().when(telegramConfiguration.getEvents()).thenReturn(Map.of());
	}
	
	@Test
	void notLoggableEventIsIgnored(){
		var event = mock(IEvent.class);
		
		tested.onEvent(event);
		
		verify(telegramApi, never()).sendMessage(any());
	}
	
	@Test
	void eventIsFiltered(){
		var event = mock(ILoggableEvent.class);
		
		when(telegramConfiguration.getEvents()).thenReturn(Map.of("unknown", mock(MessageEventConfiguration.class)));
		tested.onEvent(event);
		
		verify(telegramApi, never()).sendMessage(any());
	}
	
	@Test
	void messageIsSent(){
		var event = mock(ILoggableEvent.class);
		var eventConfiguration = mock(MessageEventConfiguration.class);
		var message = mock(Message.class);
		
		when(telegramConfiguration.getEvents()).thenReturn(Map.of(event.getClass().getSimpleName(), eventConfiguration));
		when(telegramMessageBuilder.createSimpleMessage(event, eventConfiguration, USERNAME)).thenReturn(message);
		tested.onEvent(event);
		
		verify(telegramApi).sendMessage(message);
	}
	
	@Test
	void messageIsSentStreamerEvent(){
		var event = mock(AbstractLoggableStreamerEvent.class);
		var eventConfiguration = mock(MessageEventConfiguration.class);
		var message = mock(Message.class);
		
		when(event.getStreamerUsername()).thenReturn(Optional.of("streamer"));
		when(telegramConfiguration.getEvents()).thenReturn(Map.of(event.getClass().getSimpleName(), eventConfiguration));
		when(telegramMessageBuilder.createSimpleMessage(event, eventConfiguration, USERNAME)).thenReturn(message);
		tested.onEvent(event);
		
		verify(telegramApi).sendMessage(message);
	}
	
	@Test
	void messageIsSentDefaultConfig(){
		var event = mock(ILoggableEvent.class);
		var message = mock(Message.class);
		
		when(telegramMessageBuilder.createSimpleMessage(event, null, USERNAME)).thenReturn(message);
		tested.onEvent(event);
		
		verify(telegramApi).sendMessage(message);
	}
}