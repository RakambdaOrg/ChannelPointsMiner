package fr.raksrinana.channelpointsminer.miner.api.chat.irc;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatMessageListener;
import fr.raksrinana.channelpointsminer.miner.api.chat.TwitchChatFactory;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.command.CapabilityRequestCommand;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.MessageTag;
import org.kitteh.irc.client.library.feature.EventManager;
import org.kitteh.irc.client.library.feature.MessageTagManager;
import org.kitteh.irc.client.library.util.TriFunction;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.LinkedList;
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
	private Client client;
	@Mock
    private ITwitchChatMessageListener chatMessageListener;
    @Spy
    private List<ITwitchChatMessageListener> chatMessageListeners = new LinkedList<>();
    @Spy
    private List<String> capabilities = new LinkedList<>();
    @Mock
    private TwitchChatFactory.TagCreator tagCreator;
    @Mock
    private TriFunction<Client, String, String, ? extends MessageTag> tagCreatorFunction;
    @Spy
    private List<TwitchChatFactory.TagCreator> tagCreators = new LinkedList<>();
    @Spy
	private EventManager eventManager;
    @Mock
    private MessageTagManager tagManager;
    @Mock
    private Client.Commands commands;
    @Mock
    private CapabilityRequestCommand capabilityRequestCommand;
    
    @Captor
    private ArgumentCaptor<String> capabilityCaptor1;
    @Captor
    private ArgumentCaptor<String> capabilityCaptor2;
    @Captor
    private ArgumentCaptor<String> tagCaptor;
    @Captor
    private ArgumentCaptor<TriFunction<Client, String, String, ? extends MessageTag>> tagCreatorCaptor;
    @Captor
    private ArgumentCaptor<TwitchIrcHandler> ircHandlerCaptor;
    
	@BeforeEach
	void setUp(){
        tested = new TwitchIrcChatClient(twitchLogin, capabilities, chatMessageListeners, tagCreators);
        
        lenient().when(client.getEventManager()).thenReturn(eventManager);
		lenient().when(client.commands()).thenReturn(commands);
        lenient().when(client.getMessageTagManager()).thenReturn(tagManager);
		lenient().when(commands.capabilityRequest()).thenReturn(capabilityRequestCommand);
		
		lenient().when(twitchLogin.getUsername()).thenReturn(USERNAME);
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
        
        lenient().when(tagCreator.getTagName()).thenReturn(TAG_NAME);
        lenient().when(tagCreator.getCapability()).thenReturn(CAPABILITY_NAME);
        lenient().doReturn(tagCreatorFunction).when(tagCreator).getTagCreator();
        
        tagCreators.add(tagCreator);
        tagCreators.add(tagCreator);
        
        capabilities.add(CAPABILITY_NAME);
        capabilities.add(CAPABILITY_NAME);
        
        chatMessageListeners.add(chatMessageListener);
        chatMessageListeners.add(chatMessageListener);
	}
	
	@Test
	void joinChannelCreatesClient(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createIrcClient(twitchLogin)).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			factory.verify(() -> TwitchIrcFactory.createIrcClient(twitchLogin));
			verify(client).connect();
			verify(eventManager).registerEventListener(ircHandlerCaptor.capture());
            
            verify(capabilityRequestCommand, times(capabilities.size())).enable(capabilityCaptor1.capture());
            assertThat(capabilityCaptor1.getAllValues()).containsExactlyInAnyOrder(capabilities.toArray(new String[0]));
            
            verify(tagManager, times(tagCreators.size())).registerTagCreator(capabilityCaptor2.capture(), tagCaptor.capture(), tagCreatorCaptor.capture());
            assertThat(capabilityCaptor2.getAllValues()).containsExactlyInAnyOrder(CAPABILITY_NAME, CAPABILITY_NAME);
            assertThat(tagCaptor.getAllValues()).containsExactlyInAnyOrder(TAG_NAME, TAG_NAME);
            assertThat(tagCreatorCaptor.getAllValues()).containsExactlyInAnyOrder(tagCreatorFunction, tagCreatorFunction);
            
			verify(client).addChannel(STREAMER_CHANNEL);
		}
	}
	
	@Test
	void joinChannelCreatesClientOnlyOnce(){
		try(var factory = mockStatic(TwitchIrcFactory.class)){
			factory.when(() -> TwitchIrcFactory.createIrcClient(twitchLogin)).thenReturn(client);
			
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
			
			var channel = mock(Channel.class);
			when(client.getChannel(STREAMER_CHANNEL)).thenReturn(Optional.of(channel));
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			factory.verify(() -> TwitchIrcFactory.createIrcClient(twitchLogin));
			verify(client).connect();
            
            verify(eventManager).registerEventListener(ircHandlerCaptor.capture());
            
            verify(capabilityRequestCommand, times(capabilities.size())).enable(capabilityCaptor1.capture());
            assertThat(capabilityCaptor1.getAllValues()).containsExactlyInAnyOrder(capabilities.toArray(new String[0]));
            
            verify(tagManager, times(tagCreators.size())).registerTagCreator(capabilityCaptor2.capture(), tagCaptor.capture(), tagCreatorCaptor.capture());
            assertThat(capabilityCaptor2.getAllValues()).containsExactlyInAnyOrder(CAPABILITY_NAME, CAPABILITY_NAME);
            assertThat(tagCaptor.getAllValues()).containsExactlyInAnyOrder(TAG_NAME, TAG_NAME);
            assertThat(tagCreatorCaptor.getAllValues()).containsExactlyInAnyOrder(tagCreatorFunction, tagCreatorFunction);
            
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