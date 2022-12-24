package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.StreamDown;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.StreamUp;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamDownEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamUpEvent;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.streamer.StreamerSettings;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class StreamStartEndHandlerTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_NAME = "streamer-name";
	private static final Instant NOW = Instant.parse("2020-05-17T12:14:20.000Z");
	
	@InjectMocks
	private StreamStartEndHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private Streamer streamer;
	@Mock
	private StreamerSettings streamerSettings;
	@Mock
	private Topic topic;
	@Mock
	private StreamUp streamUpMessage;
	@Mock
	private StreamDown streamDownMessage;
	@Mock
	private ITwitchChatClient chatClient;
	
	@BeforeEach
	void setUp(){
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_NAME);
		lenient().when(streamer.getSettings()).thenReturn(streamerSettings);
		lenient().when(streamerSettings.isJoinIrc()).thenReturn(false);
		lenient().when(miner.getChatClient()).thenReturn(chatClient);
		lenient().when(streamUpMessage.getServerTime()).thenReturn(NOW);
		lenient().when(streamDownMessage.getServerTime()).thenReturn(NOW);
	}
	
	@Test
	void streamUp(){
		when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return null;
		});
		
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		assertDoesNotThrow(() -> tested.handle(topic, streamUpMessage));
		
		verify(miner).updateStreamerInfos(streamer);
		verify(miner).onEvent(new StreamUpEvent(miner, STREAMER_ID, STREAMER_NAME, streamer, NOW));
		verify(chatClient, never()).join(any());
	}
	
	@Test
	void streamUpWithJoinIrc(){
		when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return null;
		});
		
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		lenient().when(streamerSettings.isJoinIrc()).thenReturn(true);
		
		assertDoesNotThrow(() -> tested.handle(topic, streamUpMessage));
		
		verify(miner).updateStreamerInfos(streamer);
		verify(miner).onEvent(new StreamUpEvent(miner, STREAMER_ID, STREAMER_NAME, streamer, NOW));
		verify(chatClient).join(STREAMER_NAME);
	}
	
	@Test
	void streamUpUnknown(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.handle(topic, streamUpMessage));
		
		verify(miner, never()).schedule(any(Runnable.class), anyLong(), any());
		verify(miner).onEvent(new StreamUpEvent(miner, STREAMER_ID, null, null, NOW));
		verify(chatClient, never()).join(any());
	}
	
	@Test
	void streamDown(){
		when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return null;
		});
		
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		assertDoesNotThrow(() -> tested.handle(topic, streamDownMessage));
		
		verify(miner).updateStreamerInfos(streamer);
		verify(miner).onEvent(new StreamDownEvent(miner, STREAMER_ID, STREAMER_NAME, streamer, NOW));
		verify(chatClient).leave(STREAMER_NAME);
	}
	
	@Test
	void streamDownUnknown(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.handle(topic, streamDownMessage));
		
		verify(miner, never()).schedule(any(Runnable.class), anyLong(), any());
		verify(miner).onEvent(new StreamDownEvent(miner, STREAMER_ID, null, null, NOW));
		verify(chatClient, never()).leave(any());
	}
}