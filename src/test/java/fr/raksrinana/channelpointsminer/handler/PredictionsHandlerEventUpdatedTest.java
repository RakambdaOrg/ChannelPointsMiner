package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.EventCreated;
import fr.raksrinana.channelpointsminer.api.ws.data.message.EventUpdated;
import fr.raksrinana.channelpointsminer.api.ws.data.message.eventcreated.EventCreatedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.eventupdated.EventUpdatedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.handler.data.Prediction;
import fr.raksrinana.channelpointsminer.handler.data.PredictionState;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacer;
import fr.raksrinana.channelpointsminer.prediction.delay.DelayCalculator;
import fr.raksrinana.channelpointsminer.streamer.PredictionSettings;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import fr.raksrinana.channelpointsminer.streamer.StreamerSettings;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import static fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.EventStatus.ACTIVE;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictionsHandlerEventUpdatedTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String EVENT_ID = "event-id";
	private static final int MINIMUM_REQUIRED = 50;
	private static final int WINDOW_SECONDS = 300;
	private static final ZonedDateTime EVENT_DATE = ZonedDateTime.of(2021, 10, 10, 11, 59, 0, 0, UTC);
	private static final ZonedDateTime EVENT_UPDATE_DATE = ZonedDateTime.of(2021, 10, 10, 11, 59, 30, 0, UTC);
	private static final ZonedDateTime NOW = ZonedDateTime.of(2021, 10, 10, 12, 0, 0, 0, UTC);
	private static final ZonedDateTime SCHEDULE_DATE = ZonedDateTime.of(2021, 10, 10, 12, 1, 0, 0, UTC);
	
	@InjectMocks
	private PredictionsHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private BetPlacer betPlacer;
	@Mock
	private EventCreated eventCreated;
	@Mock
	private EventCreatedData eventCreatedData;
	@Mock
	private EventUpdated eventUpdated;
	@Mock
	private EventUpdatedData eventUpdatedData;
	@Mock
	private Event event;
	@Mock
	private Event event2;
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
	
	@Captor
	private ArgumentCaptor<Prediction> predictionCaptor;
	
	private Prediction capturedPrediction;
	
	@BeforeEach
	void setUp(){
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		lenient().when(eventCreated.getData()).thenReturn(eventCreatedData);
		lenient().when(eventCreatedData.getEvent()).thenReturn(event);
		
		lenient().when(eventUpdated.getData()).thenReturn(eventUpdatedData);
		lenient().when(eventUpdatedData.getEvent()).thenReturn(event2);
		lenient().when(eventUpdatedData.getTimestamp()).thenReturn(EVENT_UPDATE_DATE);
		
		lenient().when(event.getId()).thenReturn(EVENT_ID);
		lenient().when(event.getStatus()).thenReturn(ACTIVE);
		lenient().when(event.getCreatedAt()).thenReturn(EVENT_DATE);
		lenient().when(event.getPredictionWindowSeconds()).thenReturn(WINDOW_SECONDS);
		
		lenient().when(event2.getId()).thenReturn(EVENT_ID);
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getChannelPoints()).thenReturn(Optional.of(MINIMUM_REQUIRED + 1));
		
		lenient().when(streamer.getSettings()).thenReturn(streamerSettings);
		lenient().when(streamerSettings.getPredictions()).thenReturn(predictionSettings);
		lenient().when(predictionSettings.getMinimumPointsRequired()).thenReturn(MINIMUM_REQUIRED);
		lenient().when(predictionSettings.getDelayCalculator()).thenReturn(delayCalculator);
		
		lenient().when(delayCalculator.calculate(event)).thenReturn(SCHEDULE_DATE);
		
		lenient().when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
			var runnable = invocation.getArgument(0, Runnable.class);
			runnable.run();
			return mock(ScheduledFuture.class);
		});
	}
	
	@Test
	void unknownEvent(){
		assertDoesNotThrow(() -> tested.handle(topic, eventUpdated));
		
		//Should verify nothing is done, but how?
	}
	
	@Test
	void unknownChannelPoints(){
		createEvent();
		
		when(eventUpdatedData.getTimestamp()).thenReturn(EVENT_DATE.minusSeconds(30));
		
		assertDoesNotThrow(() -> tested.handle(topic, eventUpdated));
		
		assertThat(capturedPrediction).isEqualTo(Prediction.builder()
				.event(event)
				.streamer(streamer)
				.state(PredictionState.SCHEDULED)
				.lastUpdate(EVENT_DATE)
				.build());
	}
	
	void createEvent(){
		tested.handle(topic, eventCreated);
		
		verify(betPlacer).placeBet(predictionCaptor.capture());
		capturedPrediction = predictionCaptor.getValue();
	}
	
	@Test
	void updatePrediction(){
		createEvent();
		
		assertDoesNotThrow(() -> tested.handle(topic, eventUpdated));
		
		assertThat(capturedPrediction).isEqualTo(Prediction.builder()
				.event(event2)
				.streamer(streamer)
				.state(PredictionState.SCHEDULED)
				.lastUpdate(EVENT_UPDATE_DATE)
				.build());
	}
	
	@Test
	void isUpdatedOnlyOnce(){
		createEvent();
		
		assertDoesNotThrow(() -> tested.handle(topic, eventUpdated));
		
		when(eventUpdatedData.getTimestamp()).thenReturn(EVENT_DATE);
		assertDoesNotThrow(() -> tested.handle(topic, eventUpdated));
		
		assertThat(capturedPrediction).isEqualTo(Prediction.builder()
				.event(event2)
				.streamer(streamer)
				.state(PredictionState.SCHEDULED)
				.lastUpdate(EVENT_UPDATE_DATE)
				.build());
	}
}