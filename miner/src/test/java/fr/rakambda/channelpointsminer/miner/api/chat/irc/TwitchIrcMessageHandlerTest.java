package fr.rakambda.channelpointsminer.miner.api.chat.irc;

import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatMessageListener;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.MessageTag;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class TwitchIrcMessageHandlerTest{
	private static final String USERNAME = "username";
	private static final String STREAMER = "streamer";
	private static final String STREAMER_CHANNEL = "#streamer";
	private static final String BADGE_INFO = "badge-info";
	private static final String MESSAGE = "message";
	
	private TwitchIrcMessageHandler tested;
	
	@Mock
	private ITwitchChatMessageListener chatMessageListener;
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
		tested = new TwitchIrcMessageHandler(USERNAME);
		tested.addListener(chatMessageListener);
		
		lenient().when(channelMessageEvent.getChannel()).thenReturn(chatChannel);
		lenient().when(chatChannel.getName()).thenReturn(STREAMER_CHANNEL);
		lenient().when(channelMessageEvent.getActor()).thenReturn(chatUser);
		lenient().when(chatUser.getMessagingName()).thenReturn(USERNAME);
		lenient().when(channelMessageEvent.getMessage()).thenReturn(MESSAGE);
	}
	
	@Test
	void messageListenersCalledWithBadge(){
		when(channelMessageEvent.getTag("badges")).thenReturn(Optional.of(messageTag));
		when(messageTag.getAsString()).thenReturn(BADGE_INFO);
		
		assertDoesNotThrow(() -> tested.onMessageEvent(channelMessageEvent));
		
		verify(chatMessageListener).onChatMessage(STREAMER, USERNAME, MESSAGE, BADGE_INFO);
	}
	
	@Test
	void messageListenersCalledWithoutBadge(){
		when(channelMessageEvent.getTag("badges")).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.onMessageEvent(channelMessageEvent));
		
		verify(chatMessageListener).onChatMessage(STREAMER, USERNAME, MESSAGE);
	}
}