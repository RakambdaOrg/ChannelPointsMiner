package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.EventCreated;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.eventcreated.EventCreatedData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.EventCreatedEvent;
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
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import static fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.EventStatus.ACTIVE;
import static fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.EventStatus.LOCKED;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class PredictionsHandlerEventCreatedTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String NON_EXISTENT_STREAMER_ID = "streamer-id2";
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
	private IEventManager eventManager;
	@Mock
	private BetPlacer betPlacer;
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
	private IDelayCalculator delayCalculator;
	
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
		lenient().when(predictionSettings.getDelayCalculator()).thenReturn(delayCalculator);
		
		lenient().when(delayCalculator.calculate(event)).thenReturn(SCHEDULE_DATE);
	}
	
	@Test
	void eventNotActive(){
		when(event.getStatus()).thenReturn(LOCKED);
		
		assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
		
		verify(miner, never()).schedule(any(), anyLong(), any());
	}
	
	@Test
	void unknownStreamer(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
		assertThat(tested.getPredictions()).isEmpty();
		
		verify(miner, never()).schedule(any(), anyLong(), any());
	}
	
	@Test
	void unknownChannelPoints(){
		when(streamer.getChannelPoints()).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
		assertThat(tested.getPredictions()).isEmpty();
		
		verify(miner, never()).schedule(any(), anyLong(), any());
	}
	
	@Test
	void notEnoughPoints(){
		when(streamer.getChannelPoints()).thenReturn(Optional.of(MINIMUM_REQUIRED - 1));
		
		assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
		assertThat(tested.getPredictions()).isEmpty();
		
		verify(miner, never()).schedule(any(), anyLong(), any());
	}
	
	@Test
	void predictionScheduled(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(miner.schedule(any(Runnable.class), anyLong(), any())).thenAnswer(invocation -> {
				var runnable = invocation.getArgument(0, Runnable.class);
				runnable.run();
				return mock(ScheduledFuture.class);
			});
			
			var expectedPrediction = BettingPrediction.builder()
					.event(event)
					.streamer(streamer)
					.state(PredictionState.SCHEDULED)
					.lastUpdate(EVENT_DATE)
					.build();
			
			assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
			assertThat(tested.getPredictions()).containsOnly(Map.entry(EVENT_ID, expectedPrediction));
			
			verify(miner).schedule(any(), eq(60L), eq(TimeUnit.SECONDS));
			verify(eventManager).onEvent(new EventCreatedEvent(streamer, event));
			verify(betPlacer).placeBet(expectedPrediction);
		}
	}
	
	@Test
	void predictionScheduledTooEarly(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			lenient().when(delayCalculator.calculate(event)).thenReturn(EVENT_DATE);
			
			assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
			assertThat(tested.getPredictions()).isNotEmpty();
			
			verify(miner).schedule(any(), eq(5L), eq(TimeUnit.SECONDS));
			verify(eventManager).onEvent(new EventCreatedEvent(streamer, event));
		}
	}
	
	@Test
	void predictionScheduledTooLate(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			lenient().when(delayCalculator.calculate(event)).thenReturn(EVENT_DATE.plusSeconds(WINDOW_SECONDS));
			
			assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
			assertThat(tested.getPredictions()).isNotEmpty();
			
			verify(miner).schedule(any(), eq(WINDOW_SECONDS - 60L - 5L), eq(TimeUnit.SECONDS));
			verify(eventManager).onEvent(new EventCreatedEvent(streamer, event));
		}
	}
	
	@Test
	void predictionScheduledOnlyOnce(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			tested.getPredictions().put(EVENT_ID, mock(BettingPrediction.class));
			
			assertDoesNotThrow(() -> tested.handle(topic, eventCreated));
			assertThat(tested.getPredictions()).hasSize(1);
			
			verify(miner, never()).schedule(any(), anyLong(), any());
			verify(eventManager, never()).onEvent(any());
		}
	}
}