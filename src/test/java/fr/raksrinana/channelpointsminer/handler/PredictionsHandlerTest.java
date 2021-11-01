package fr.raksrinana.twitchminer.handler;

import fr.raksrinana.twitchminer.api.ws.data.message.EventCreated;
import fr.raksrinana.twitchminer.api.ws.data.message.eventcreated.EventCreatedData;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.factory.TimeFactory;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.prediction.DelayCalculator;
import fr.raksrinana.twitchminer.streamer.PredictionSettings;
import fr.raksrinana.twitchminer.streamer.Streamer;
import fr.raksrinana.twitchminer.streamer.StreamerSettings;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.twitchminer.api.ws.data.message.subtype.EventStatus.ACTIVE;
import static fr.raksrinana.twitchminer.api.ws.data.message.subtype.EventStatus.LOCKED;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictionsHandlerTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String EVENT_ID = "event-id";
	private static final int MINIMUM_REQUIRED = 50;
	private static final int WINDOW_SECONDS = 300;
	private static final ZonedDateTime EVENT_DATE = ZonedDateTime.of(2021, 10, 10, 11, 59, 0, 0, UTC);
	private static final ZonedDateTime NOW = ZonedDateTime.of(2021, 10, 10, 12, 0, 0, 0, UTC);
	private static final ZonedDateTime SCHEDULE_DATE = ZonedDateTime.of(2021, 10, 10, 12, 1, 0, 0, UTC);
	
	@InjectMocks
	private PredictionsHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private EventCreated eventCreated;
	@Mock
	private EventCreatedData eventCreatedData;
	@Mock
	private Event event;
	@Mock
	private Topic topic;
	@Mock
	private Streamer streamer;
	@Mock
	private StreamerSettings streamerSettings;
	@Mock
	private PredictionSettings predictionSettings;
	@Mock
	private DelayCalculator delayCalculator;
	
	@BeforeEach
	void setUp(){
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		lenient().when(eventCreated.getData()).thenReturn(eventCreatedData);
		lenient().when(eventCreatedData.getEvent()).thenReturn(event);
		
		lenient().when(event.getId()).thenReturn(EVENT_ID);
		lenient().when(event.getStatus()).thenReturn(ACTIVE);
		lenient().when(event.getCreatedAt()).thenReturn(EVENT_DATE);
		lenient().when(event.getPredictionWindowSeconds()).thenReturn(WINDOW_SECONDS);
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getChannelPoints()).thenReturn(Optional.of(MINIMUM_REQUIRED + 1));
		
		lenient().when(streamer.getSettings()).thenReturn(streamerSettings);
		lenient().when(streamerSettings.getPredictions()).thenReturn(predictionSettings);
		lenient().when(predictionSettings.getMinimumPointsRequired()).thenReturn(MINIMUM_REQUIRED);
		lenient().when(predictionSettings.getDelay()).thenReturn(delayCalculator);
		
		lenient().when(delayCalculator.calculate(event)).thenReturn(SCHEDULE_DATE);
	}
	
	@Test
	void eventNotActive(){
		when(event.getStatus()).thenReturn(LOCKED);
		
		assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
		
		verify(miner, never()).getStreamerById(any());
		verify(miner, never()).schedule(any(), anyLong(), any());
	}
	
	@Test
	void unknownStreamer(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
		
		verify(miner, never()).schedule(any(), anyLong(), any());
	}
	
	@Test
	void unknownChannelPoints(){
		when(streamer.getChannelPoints()).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
		
		verify(miner, never()).schedule(any(), anyLong(), any());
	}
	
	@Test
	void notEnoughPoints(){
		when(streamer.getChannelPoints()).thenReturn(Optional.of(MINIMUM_REQUIRED - 1));
		
		assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
		
		verify(miner, never()).schedule(any(), anyLong(), any());
	}
	
	@Test
	void predictionScheduled(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
			
			verify(miner).schedule(any(), eq(60L), eq(TimeUnit.SECONDS));
		}
	}
	
	@Test
	void predictionScheduledTooEarly(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			lenient().when(delayCalculator.calculate(event)).thenReturn(EVENT_DATE);
			
			assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
			
			verify(miner).schedule(any(), eq(5L), eq(TimeUnit.SECONDS));
		}
	}
	
	@Test
	void predictionScheduledTooLate(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			lenient().when(delayCalculator.calculate(event)).thenReturn(EVENT_DATE.plusSeconds(WINDOW_SECONDS));
			
			assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
			
			verify(miner).schedule(any(), eq(WINDOW_SECONDS - 60L - 5L), eq(TimeUnit.SECONDS));
		}
	}
	
	@Test
	void predictionScheduledOnlyOnce(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
			assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
			
			verify(miner).schedule(any(), eq(60L), eq(TimeUnit.SECONDS));
		}
	}
}