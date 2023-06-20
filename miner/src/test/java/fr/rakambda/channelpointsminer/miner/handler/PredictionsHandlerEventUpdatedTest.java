package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.EventUpdated;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.eventupdated.EventUpdatedData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.EventStatus;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.EventCreatedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.EventUpdatedEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.handler.data.PredictionState;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.prediction.bet.BetPlacer;
import fr.rakambda.channelpointsminer.miner.prediction.delay.IDelayCalculator;
import fr.rakambda.channelpointsminer.miner.streamer.PredictionSettings;
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
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class PredictionsHandlerEventUpdatedTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-username";
	private static final String EVENT_ID = "event-id";
	private static final int MINIMUM_REQUIRED = 50;
	private static final int WINDOW_SECONDS = 300;
	private static final ZonedDateTime EVENT_DATE = ZonedDateTime.of(2021, 10, 10, 11, 59, 0, 0, UTC);
	private static final ZonedDateTime EVENT_UPDATE_DATE = ZonedDateTime.of(2021, 10, 10, 11, 59, 30, 0, UTC);
	private static final ZonedDateTime SCHEDULE_DATE = ZonedDateTime.of(2021, 10, 10, 12, 1, 0, 0, UTC);
	private static final Instant NOW = Instant.parse("2021-10-10T12:00:00.000Z");
	private static final ZonedDateTime NOW_ZONED = ZonedDateTime.ofInstant(NOW, UTC);
	
	@InjectMocks
	private PredictionsHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private IEventManager eventManager;
	@Mock
	private BetPlacer betPlacer;
	@Mock
	private EventUpdated eventUpdated;
	@Mock
	private EventUpdatedData eventUpdatedData;
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
	private IDelayCalculator delayCalculator;
	
	@BeforeEach
	void setUp(){
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		lenient().when(eventUpdated.getData()).thenReturn(eventUpdatedData);
		lenient().when(eventUpdatedData.getEvent()).thenReturn(event);
		lenient().when(eventUpdatedData.getTimestamp()).thenReturn(EVENT_UPDATE_DATE);
		
		lenient().when(event.getId()).thenReturn(EVENT_ID);
		lenient().when(event.getStatus()).thenReturn(EventStatus.ACTIVE);
		lenient().when(event.getCreatedAt()).thenReturn(EVENT_DATE);
		lenient().when(event.getPredictionWindowSeconds()).thenReturn(WINDOW_SECONDS);
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
		lenient().when(streamer.getSettings()).thenReturn(streamerSettings);
		lenient().when(streamer.getChannelPoints()).thenReturn(Optional.of(MINIMUM_REQUIRED + 1));
		
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
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW_ZONED);
            timeFactory.when(TimeFactory::now).thenReturn(NOW);
            
			var expectedPrediction = BettingPrediction.builder()
					.event(event)
					.streamer(streamer)
					.state(PredictionState.SCHEDULED)
					.lastUpdate(EVENT_DATE)
					.build();
			
			assertDoesNotThrow(() -> tested.handle(topic, eventUpdated));
			assertThat(tested.getPredictions()).containsOnly(Map.entry(EVENT_ID, expectedPrediction));
			
			verify(miner).schedule(any(), eq(60L), eq(TimeUnit.SECONDS));
			verify(eventManager).onEvent(new EventCreatedEvent(streamer, event));
			verify(eventManager).onEvent(new EventUpdatedEvent(NOW, STREAMER_USERNAME, event));
			verify(betPlacer).placeBet(expectedPrediction);
		}
	}
	
	@Test
	void unknownEventUnknownStreamer(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		assertDoesNotThrow(() -> tested.handle(topic, eventUpdated));
		assertThat(tested.getPredictions()).isEmpty();
	}
	
	@Test
	void updatePrediction(){
		createEvent();
		
		assertDoesNotThrow(() -> tested.handle(topic, eventUpdated));
		
		assertThat(tested.getPredictions()).containsOnly(Map.entry(EVENT_ID, BettingPrediction.builder()
				.event(event)
				.streamer(streamer)
				.state(PredictionState.SCHEDULED)
				.lastUpdate(EVENT_UPDATE_DATE)
				.build()));
	}
	
	private void createEvent(){
		tested.getPredictions().put(EVENT_ID, createDefaultPrediction());
	}
	
	private BettingPrediction createDefaultPrediction(){
		return BettingPrediction.builder()
				.event(event)
				.streamer(streamer)
				.state(PredictionState.SCHEDULED)
				.lastUpdate(EVENT_DATE)
				.build();
	}
	
	@Test
	void eventIsNotTheLatest(){
		createEvent();
		
		when(eventUpdatedData.getTimestamp()).thenReturn(EVENT_DATE.minusSeconds(30));
		
		assertDoesNotThrow(() -> tested.handle(topic, eventUpdated));
		assertThat(tested.getPredictions()).containsOnly(Map.entry(EVENT_ID, createDefaultPrediction()));
	}
	
	@Test
	void eventUpdatedEvent(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			createEvent();
			
			assertDoesNotThrow(() -> tested.handle(topic, eventUpdated));
			verify(eventManager).onEvent(new EventUpdatedEvent(NOW, STREAMER_USERNAME, event));
		}
	}
}