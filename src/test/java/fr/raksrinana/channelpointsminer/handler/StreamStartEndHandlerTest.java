package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.StreamDown;
import fr.raksrinana.channelpointsminer.api.ws.data.message.StreamUp;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.event.impl.StreamDownEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamUpEvent;
import fr.raksrinana.channelpointsminer.irc.TwitchIrcClient;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import fr.raksrinana.channelpointsminer.streamer.StreamerSettings;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreamStartEndHandlerTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_NAME = "streamer-name";
	
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
	private TwitchIrcClient ircClient;
	
	@BeforeEach
	void setUp(){
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_NAME);
		lenient().when(streamer.getSettings()).thenReturn(streamerSettings);
		lenient().when(streamerSettings.isJoinIrc()).thenReturn(false);
		lenient().when(miner.getIrcClient()).thenReturn(ircClient);
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
		verify(miner).onEvent(new StreamUpEvent(miner, STREAMER_ID, STREAMER_NAME, streamer));
		verify(ircClient, never()).join(any());
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
		verify(miner).onEvent(new StreamUpEvent(miner, STREAMER_ID, STREAMER_NAME, streamer));
		verify(ircClient).join(STREAMER_NAME);
	}
	
	@Test
	void streamUpUnknown(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.handle(topic, streamUpMessage));
		
		verify(miner, never()).schedule(any(Runnable.class), anyLong(), any());
		verify(miner).onEvent(new StreamUpEvent(miner, STREAMER_ID, null, null));
		verify(ircClient, never()).join(any());
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
		verify(miner).onEvent(new StreamDownEvent(miner, STREAMER_ID, STREAMER_NAME, streamer));
		verify(ircClient).leave(STREAMER_NAME);
	}
	
	@Test
	void streamDownUnknown(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.handle(topic, streamDownMessage));
		
		verify(miner, never()).schedule(any(Runnable.class), anyLong(), any());
		verify(miner).onEvent(new StreamDownEvent(miner, STREAMER_ID, null, null));
		verify(ircClient, never()).leave(any());
	}
}