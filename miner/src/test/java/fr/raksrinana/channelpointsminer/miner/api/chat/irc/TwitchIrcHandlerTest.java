package fr.raksrinana.channelpointsminer.miner.api.chat.irc;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatMessageListener;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.BeforeEach;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.MessageTag;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionClosedEvent;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class TwitchIrcHandlerTest{
	private static final String USERNAME = "username";
    private static final String STREAMER = "streamer";
    private static final String STREAMER_CHANNEL = "#streamer";
    private static final String BADGE_INFO = "badge-info";
    private static final String MESSAGE = "message";
    
	private TwitchIrcHandler tested;
    @Mock
    private ITwitchChatMessageListener chatMessageListener;
    @Mock
	private ClientConnectionClosedEvent clientConnectionClosedEvent;
    @Mock
    private ChannelMessageEvent channelMessageEvent;
    @Mock
    private Channel chatChannel;
    @Mock
    private User chatUser;
    @Mock
    private MessageTag messageTag;
    
    @BeforeEach
    void setUp(){
    
        var chatMessageListeners = Collections.singletonList(chatMessageListener);
        tested = new TwitchIrcHandler(USERNAME, chatMessageListeners);
    
        lenient().when(channelMessageEvent.getChannel()).thenReturn(chatChannel);
        lenient().when(chatChannel.getName()).thenReturn(STREAMER_CHANNEL);
        lenient().when(channelMessageEvent.getActor()).thenReturn(chatUser);
        lenient().when(chatUser.getMessagingName()).thenReturn(USERNAME);
        lenient().when(channelMessageEvent.getMessage()).thenReturn(MESSAGE);
    }
    
	@Test
	void reconnectIfAttemptCanBeMade(){
		when(clientConnectionClosedEvent.canAttemptReconnect()).thenReturn(true);
		
		assertDoesNotThrow(() -> tested.onClientConnectionCLoseEvent(clientConnectionClosedEvent));
		
		verify(clientConnectionClosedEvent).setAttemptReconnect(true);
	}
	
	@Test
	void cannotReconnect(){
		when(clientConnectionClosedEvent.canAttemptReconnect()).thenReturn(false);
		
		assertDoesNotThrow(() -> tested.onClientConnectionCLoseEvent(clientConnectionClosedEvent));
		
		verify(clientConnectionClosedEvent, never()).setAttemptReconnect(anyBoolean());
	}
    
    @Test
    void messageListenersCalledWithBadge(){
        when(channelMessageEvent.getTag("badges")).thenReturn(Optional.of(messageTag));
        when(messageTag.getAsString()).thenReturn(BADGE_INFO);
    
        assertDoesNotThrow(() -> tested.onMessageEvent(channelMessageEvent));
        
        verify(chatMessageListener).processMessage(STREAMER, USERNAME, MESSAGE, BADGE_INFO);
    }
    
    @Test
    void messageListenersCalledWithoutBadge(){
        when(channelMessageEvent.getTag("badges")).thenReturn(Optional.empty());
        
        assertDoesNotThrow(() -> tested.onMessageEvent(channelMessageEvent));
        
        verify(chatMessageListener).processMessage(STREAMER, USERNAME, MESSAGE);
    }
}