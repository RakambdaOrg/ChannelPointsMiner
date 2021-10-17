package fr.raksrinana.twitchminer.miner.handler;

import fr.raksrinana.twitchminer.api.ws.data.message.StreamDown;
import fr.raksrinana.twitchminer.api.ws.data.message.StreamUp;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreamStartEndHandlerTest{
	private static final String STREAMER_ID = "streamer-id";
	
	@InjectMocks
	private StreamStartEndHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private Streamer streamer;
	@Mock
	private Topic topic;
	@Mock
	private StreamUp streamUpMessage;
	@Mock
	private StreamDown streamDownMessage;
	
	@BeforeEach
	void setUp(){
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void streamUp(){
		when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return null;
		});
		
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		assertDoesNotThrow(() -> tested.onStreamUp(topic, streamUpMessage));
		
		verify(miner).updateStreamerInfos(streamer);
	}
	
	@Test
	void streamUpUnknown(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.onStreamUp(topic, streamUpMessage));
		
		verify(miner, never()).schedule(any(Runnable.class), anyLong(), any());
	}
	
	@Test
	void streamDown(){
		when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return null;
		});
		
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		assertDoesNotThrow(() -> tested.onStreamDown(topic, streamDownMessage));
		
		verify(miner).updateStreamerInfos(streamer);
	}
	
	@Test
	void streamDownUnknown(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.onStreamDown(topic, streamDownMessage));
		
		verify(miner, never()).schedule(any(Runnable.class), anyLong(), any());
	}
}