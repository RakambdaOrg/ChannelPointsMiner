package fr.rakambda.channelpointsminer.miner.log.discord;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
import fr.rakambda.channelpointsminer.miner.api.discord.data.Webhook;
import fr.rakambda.channelpointsminer.miner.config.DiscordConfiguration;
import fr.rakambda.channelpointsminer.miner.config.DiscordEventConfiguration;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class DiscordEventListenerTest{
	@InjectMocks
	private DiscordEventListener tested;
	
	@Mock
	private DiscordApi discordApi;
	@Mock
	private DiscordConfiguration discordConfiguration;
	@Mock
	private DiscordMessageBuilder discordMessageBuilder;
	
	@BeforeEach
	void setUp(){
		lenient().when(discordConfiguration.isEmbeds()).thenReturn(false);
		lenient().when(discordConfiguration.getEvents()).thenReturn(Map.of());
	}
	
	@Test
	void notLoggableEventIsIgnored(){
		var event = mock(IEvent.class);
		
		tested.onEvent(event);
		
		verify(discordApi, never()).sendMessage(any());
	}
	
	@Test
	void eventIsFiltered(){
		var event = mock(ILoggableEvent.class);
		
		when(discordConfiguration.getEvents()).thenReturn(Map.of("unknown", mock(DiscordEventConfiguration.class)));
		tested.onEvent(event);
		
		verify(discordApi, never()).sendMessage(any());
	}
	
	@Test
	void messageIsSent(){
		var event = mock(ILoggableEvent.class);
		var eventConfiguration = mock(DiscordEventConfiguration.class);
		var webhook = mock(Webhook.class);
		
		when(discordConfiguration.getEvents()).thenReturn(Map.of(event.getClass().getSimpleName(), eventConfiguration));
		when(discordMessageBuilder.createSimpleMessage(event, eventConfiguration)).thenReturn(webhook);
		tested.onEvent(event);
		
		verify(discordApi).sendMessage(webhook);
	}
	
	@Test
	void messageIsSentDefaultConfig(){
		var event = mock(ILoggableEvent.class);
		var webhook = mock(Webhook.class);
		
		when(discordMessageBuilder.createSimpleMessage(event, null)).thenReturn(webhook);
		tested.onEvent(event);
		
		verify(discordApi).sendMessage(webhook);
	}
	
	@Test
	void embedIsSent(){
		var event = mock(ILoggableEvent.class);
		var eventConfiguration = mock(DiscordEventConfiguration.class);
		var webhook = mock(Webhook.class);
		
		when(discordConfiguration.isEmbeds()).thenReturn(true);
		when(discordConfiguration.getEvents()).thenReturn(Map.of(event.getClass().getSimpleName(), eventConfiguration));
		when(discordMessageBuilder.createEmbedMessage(event, eventConfiguration)).thenReturn(webhook);
		tested.onEvent(event);
		
		verify(discordApi).sendMessage(webhook);
	}
	
	@Test
	void embedIsSentDefaultConfig(){
		var event = mock(ILoggableEvent.class);
		var webhook = mock(Webhook.class);
		
		when(discordConfiguration.isEmbeds()).thenReturn(true);
		when(discordMessageBuilder.createEmbedMessage(event, null)).thenReturn(webhook);
		tested.onEvent(event);
		
		verify(discordApi).sendMessage(webhook);
	}
}