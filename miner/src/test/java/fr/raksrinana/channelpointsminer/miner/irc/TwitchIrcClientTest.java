package fr.raksrinana.channelpointsminer.miner.irc;

import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.command.CapabilityRequestCommand;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.feature.EventManager;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class TwitchIrcClientTest{
	private static final String USERNAME = "username";
	private static final String ACCESS_TOKEN = "password";
	private static final String STREAMER = "streamer";
	private static final String STREAMER_CHANNEL = "#streamer";
	private static final List<String> CAPABILITIES = Arrays.asList("cap1", "cap2");
	private static final List<Object> HANDLERS = Arrays.asList(new Object(), new Object());
	
	@InjectMocks
	private TwitchIrcClient tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private Client client;
	@Mock
    private TwitchIrcClientPrototype twitchIrcClientPrototype;
	@Spy
	private EventManager eventManager;
	@Mock
    private Client.Commands commands;
    @Mock
    private CapabilityRequestCommand capabilityRequestCommand;
	
	
    @Captor
    private ArgumentCaptor<String> capabilityCaptor;
    @Captor
    private ArgumentCaptor<Object> handlerCaptor;
    
	@BeforeEach
	void setUp(){
	    lenient().when(twitchIrcClientPrototype.getCapabilities()).thenReturn(CAPABILITIES);
	    lenient().when(twitchIrcClientPrototype.getHandlers()).thenReturn(HANDLERS);
	    
		lenient().when(client.getEventManager()).thenReturn(eventManager);
		lenient().when(client.commands()).thenReturn(commands);
		lenient().when(commands.capabilityRequest()).thenReturn(capabilityRequestCommand);
		
		lenient().when(twitchLogin.getUsername()).thenReturn(USERNAME);
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void joinChannelCreatesClient(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createClient(twitchLogin)).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			factory.verify(() -> TwitchIrcFactory.createClient(twitchLogin));
			verify(client).connect();
			verify(eventManager, times(HANDLERS.size())).registerEventListener(handlerCaptor.capture());
            assertThat(handlerCaptor.getAllValues()).containsExactlyInAnyOrder(HANDLERS.toArray());
            verify(capabilityRequestCommand, times(CAPABILITIES.size())).enable(capabilityCaptor.capture());
            assertThat(capabilityCaptor.getAllValues()).containsExactlyInAnyOrder(CAPABILITIES.toArray(new String[0]));
			verify(client).addChannel(STREAMER_CHANNEL);
		}
	}
	
	@Test
	void joinChannelCreatesClientOnlyOnce(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createClient(twitchLogin)).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			factory.verify(() -> TwitchIrcFactory.createClient(twitchLogin));
			verify(client).connect();
			verify(client, times(2)).addChannel(STREAMER_CHANNEL);
		}
	}
	
	@Test
	void joinChannelAlreadyJoined(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createClient(twitchLogin)).thenReturn(client);
			
			var channel = mock(Channel.class);
			when(client.getChannel(STREAMER_CHANNEL)).thenReturn(Optional.of(channel));
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			factory.verify(() -> TwitchIrcFactory.createClient(twitchLogin));
			verify(client).connect();
            verify(eventManager, times(HANDLERS.size())).registerEventListener(handlerCaptor.capture());
            assertThat(handlerCaptor.getAllValues()).containsExactlyInAnyOrder(HANDLERS.toArray());
            verify(capabilityRequestCommand, times(CAPABILITIES.size())).enable(capabilityCaptor.capture());
            assertThat(capabilityCaptor.getAllValues()).containsExactlyInAnyOrder(CAPABILITIES.toArray(new String[0]));
			verify(client, never()).addChannel(any());
		}
	}
	
	@Test
	void leaveWhenNotOpened(){
		assertDoesNotThrow(() -> tested.leave(STREAMER));
	}
	
	@Test
	void leaveChannel(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createClient(twitchLogin)).thenReturn(client);
			
			var channel = mock(Channel.class);
			when(client.getChannel(STREAMER_CHANNEL)).thenReturn(Optional.of(channel));
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			assertDoesNotThrow(() -> tested.leave(STREAMER));
			
			verify(client).connect();
			verify(client).removeChannel(STREAMER_CHANNEL);
		}
	}
	
	@Test
	void leaveNotJoinedChannel(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createClient(twitchLogin)).thenReturn(client);
			
			when(client.getChannel(STREAMER_CHANNEL)).thenReturn(Optional.empty());
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			assertDoesNotThrow(() -> tested.leave(STREAMER));
			
			verify(client).connect();
			verify(client, never()).removeChannel(any());
		}
	}
	
	@Test
	void close(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createClient(twitchLogin)).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			assertDoesNotThrow(() -> tested.close());
			
			verify(client).connect();
			verify(client).shutdown();
		}
	}
	
	@Test
	void closeNotOpened(){
		assertDoesNotThrow(() -> tested.close());
	}
}