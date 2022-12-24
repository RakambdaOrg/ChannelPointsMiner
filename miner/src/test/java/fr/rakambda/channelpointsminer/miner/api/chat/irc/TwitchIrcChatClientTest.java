package fr.rakambda.channelpointsminer.miner.api.chat.irc;

import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatMessageListener;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.command.CapabilityRequestCommand;
import org.kitteh.irc.client.library.defaults.element.messagetag.DefaultMessageTagLabel;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.feature.EventManager;
import org.kitteh.irc.client.library.feature.MessageTagManager;
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

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class TwitchIrcChatClientTest{
	private static final String USERNAME = "username";
	private static final String ACCESS_TOKEN = "password";
	private static final String STREAMER = "streamer";
	private static final String STREAMER_CHANNEL = "#streamer";
	private static final String TAG_NAME = "tag";
	private static final String CAPABILITY_NAME = "cap";
	
	private TwitchIrcChatClient tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private TwitchIrcConnectionHandler twitchIrcConnectionHandler;
	@Mock
	private TwitchIrcMessageHandler twitchIrcMessageHandler;
	@Mock
	private Client client;
	@Mock
	private EventManager eventManager;
	@Mock
	private MessageTagManager tagManager;
	@Mock
	private Client.Commands commands;
	@Mock
	private CapabilityRequestCommand capabilityRequestCommand;
	@Mock
	private ITwitchChatMessageListener chatMessageListener;
	
	@BeforeEach
	void setUp(){
		tested = new TwitchIrcChatClient(twitchLogin, false);
		
		lenient().when(client.getEventManager()).thenReturn(eventManager);
		lenient().when(client.commands()).thenReturn(commands);
		lenient().when(client.getMessageTagManager()).thenReturn(tagManager);
		lenient().when(commands.capabilityRequest()).thenReturn(capabilityRequestCommand);
		
		lenient().when(twitchLogin.getUsername()).thenReturn(USERNAME);
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void joinChannelCreatesClient(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createIrcClient(twitchLogin)).thenReturn(client);
			factory.when(() -> TwitchIrcFactory.createIrcConnectionHandler(USERNAME)).thenReturn(twitchIrcConnectionHandler);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			factory.verify(() -> TwitchIrcFactory.createIrcClient(twitchLogin));
			
			verify(client).connect();
			verify(eventManager).registerEventListener(twitchIrcConnectionHandler);
			verify(capabilityRequestCommand, never()).enable(any());
			verify(tagManager, never()).registerTagCreator(any(), any(), any());
			verify(client).addChannel(STREAMER_CHANNEL);
		}
	}
	
	@Test
	void joinChannelCreatesClientWithMessageListening(){
		tested = new TwitchIrcChatClient(twitchLogin, true);
		
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createIrcClient(twitchLogin)).thenReturn(client);
			factory.when(() -> TwitchIrcFactory.createIrcConnectionHandler(USERNAME)).thenReturn(twitchIrcConnectionHandler);
			factory.when(() -> TwitchIrcFactory.createIrcMessageHandler(USERNAME)).thenReturn(twitchIrcMessageHandler);
			
			tested.addChatMessageListener(chatMessageListener);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			factory.verify(() -> TwitchIrcFactory.createIrcClient(twitchLogin));
			verify(client).connect();
			verify(eventManager).registerEventListener(twitchIrcConnectionHandler);
			verify(eventManager).registerEventListener(twitchIrcMessageHandler);
			
			verify(capabilityRequestCommand).enable("twitch.tv/tags");
			
			verify(tagManager).registerTagCreator("twitch.tv/tags", "emote-sets", DefaultMessageTagLabel.FUNCTION);
			
			verify(client).addChannel(STREAMER_CHANNEL);
			verify(twitchIrcMessageHandler).addListener(chatMessageListener);
		}
	}
	
	@Test
	void addMessageListenerPropagatesListener(){
		tested = new TwitchIrcChatClient(twitchLogin, true);
		
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createIrcClient(twitchLogin)).thenReturn(client);
			factory.when(() -> TwitchIrcFactory.createIrcConnectionHandler(USERNAME)).thenReturn(twitchIrcConnectionHandler);
			factory.when(() -> TwitchIrcFactory.createIrcMessageHandler(USERNAME)).thenReturn(twitchIrcMessageHandler);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			verify(twitchIrcMessageHandler, never()).addListener(chatMessageListener);
			
			tested.addChatMessageListener(chatMessageListener);
			verify(twitchIrcMessageHandler).addListener(chatMessageListener);
		}
	}
	
	@Test
	void joinChannelCreatesClientOnlyOnce(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createIrcClient(twitchLogin)).thenReturn(client);
			factory.when(() -> TwitchIrcFactory.createIrcConnectionHandler(USERNAME)).thenReturn(twitchIrcConnectionHandler);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			factory.verify(() -> TwitchIrcFactory.createIrcClient(twitchLogin));
			verify(client).connect();
			verify(client, times(2)).addChannel(STREAMER_CHANNEL);
		}
	}
	
	@Test
	void joinChannelAlreadyJoined(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createIrcClient(twitchLogin)).thenReturn(client);
			factory.when(() -> TwitchIrcFactory.createIrcConnectionHandler(USERNAME)).thenReturn(twitchIrcConnectionHandler);
			
			var channel = mock(Channel.class);
			when(client.getChannel(STREAMER_CHANNEL)).thenReturn(Optional.of(channel));
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			factory.verify(() -> TwitchIrcFactory.createIrcClient(twitchLogin));
			verify(client).connect();
			
			verify(eventManager).registerEventListener(twitchIrcConnectionHandler);
			
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
			factory.when(() -> TwitchIrcFactory.createIrcClient(twitchLogin)).thenReturn(client);
			factory.when(() -> TwitchIrcFactory.createIrcConnectionHandler(USERNAME)).thenReturn(twitchIrcConnectionHandler);
			
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
			factory.when(() -> TwitchIrcFactory.createIrcClient(twitchLogin)).thenReturn(client);
			factory.when(() -> TwitchIrcFactory.createIrcConnectionHandler(USERNAME)).thenReturn(twitchIrcConnectionHandler);
			
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
			factory.when(() -> TwitchIrcFactory.createIrcClient(twitchLogin)).thenReturn(client);
			factory.when(() -> TwitchIrcFactory.createIrcConnectionHandler(USERNAME)).thenReturn(twitchIrcConnectionHandler);
			
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