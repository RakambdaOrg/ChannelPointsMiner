package fr.raksrinana.channelpointsminer.irc;

import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.feature.EventManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwitchIrcClientTest{
	private static final String USERNAME = "username";
	private static final String ACCESS_TOKEN = "password";
	private static final String STREAMER = "streamer";
	private static final String STREAMER_CHANNEL = "#streamer";
	
	@InjectMocks
	private TwitchIrcClient tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private Client client;
	@Mock
	private EventManager eventManager;
	@Mock
	private TwitchIrcEventListener listener;
	
	@BeforeEach
	void setUp(){
		lenient().when(client.getEventManager()).thenReturn(eventManager);
		
		lenient().when(twitchLogin.getUsername()).thenReturn(USERNAME);
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void joinChannelCreatesClient(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createClient(twitchLogin)).thenReturn(client);
			factory.when(() -> TwitchIrcFactory.createListener(USERNAME)).thenReturn(listener);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			factory.verify(() -> TwitchIrcFactory.createClient(twitchLogin));
			verify(client).connect();
			verify(eventManager).registerEventListener(listener);
			verify(client).addChannel(STREAMER_CHANNEL);
		}
	}
	
	@Test
	void joinChannelCreatesClientOnlyOnce(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createClient(twitchLogin)).thenReturn(client);
			factory.when(() -> TwitchIrcFactory.createListener(USERNAME)).thenReturn(listener);
			
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
			factory.when(() -> TwitchIrcFactory.createListener(USERNAME)).thenReturn(listener);
			
			var channel = mock(Channel.class);
			when(client.getChannel(STREAMER_CHANNEL)).thenReturn(Optional.of(channel));
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			factory.verify(() -> TwitchIrcFactory.createClient(twitchLogin));
			verify(client).connect();
			verify(eventManager).registerEventListener(listener);
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
			factory.when(() -> TwitchIrcFactory.createListener(USERNAME)).thenReturn(listener);
			
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
			factory.when(() -> TwitchIrcFactory.createListener(USERNAME)).thenReturn(listener);
			
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
			factory.when(() -> TwitchIrcFactory.createListener(USERNAME)).thenReturn(listener);
			
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