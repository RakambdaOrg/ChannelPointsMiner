package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.PointsEarned;
import fr.raksrinana.channelpointsminer.api.ws.data.message.PointsSpent;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.Balance;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsspent.PointsSpentData;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.event.impl.PointsEarnedEvent;
import fr.raksrinana.channelpointsminer.event.impl.PointsSpentEvent;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.ZonedDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointsHandlerTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String CHANNEL_NAME = "channel-name";
	private static final ZonedDateTime NOW = ZonedDateTime.now();
	
	@InjectMocks
	private PointsHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private Streamer streamer;
	@Mock
	private Topic topic;
	@Mock
	private PointsEarned pointsEarnedMessage;
	@Mock
	private PointsEarnedData pointsEarnedData;
	@Mock
	private PointsSpent pointsSpentMessage;
	@Mock
	private PointsSpentData pointsSpentData;
	@Mock
	private Balance balance;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		lenient().when(pointsEarnedMessage.getData()).thenReturn(pointsEarnedData);
		lenient().when(pointsEarnedData.getChannelId()).thenReturn(STREAMER_ID);
		lenient().when(pointsEarnedData.getTimestamp()).thenReturn(NOW);
		lenient().when(pointsSpentMessage.getData()).thenReturn(pointsSpentData);
		lenient().when(pointsSpentData.getBalance()).thenReturn(balance);
		lenient().when(pointsSpentData.getTimestamp()).thenReturn(NOW);
		lenient().when(balance.getChannelId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getUsername()).thenReturn(CHANNEL_NAME);
	}
	
	@Test
	void pointsEarned(){
		assertDoesNotThrow(() -> tested.handle(topic, pointsEarnedMessage));
		
		verify(miner).onEvent(new PointsEarnedEvent(miner, STREAMER_ID, CHANNEL_NAME, streamer, pointsEarnedData));
	}
	
	@Test
	void pointsSpent(){
		assertDoesNotThrow(() -> tested.handle(topic, pointsSpentMessage));
		
		verify(miner).onEvent(new PointsSpentEvent(miner, STREAMER_ID, CHANNEL_NAME, streamer, pointsSpentData));
	}
}