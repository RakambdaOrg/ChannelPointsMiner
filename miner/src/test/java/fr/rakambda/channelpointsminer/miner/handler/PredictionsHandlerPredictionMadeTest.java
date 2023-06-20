package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.PredictionMade;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.predictionmade.PredictionMadeData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Prediction;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionMadeEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.handler.data.PlacedPrediction;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.prediction.bet.BetPlacer;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import static fr.rakambda.channelpointsminer.miner.handler.data.PredictionState.PLACED;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class PredictionsHandlerPredictionMadeTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String CHANNEL_NAME = "channel-name";
	private static final String EVENT_ID = "event-id";
	private static final String OUTCOME_ID = "outcome-id";
	private static final Instant PREDICTION_DATE = Instant.parse("2020-05-17T12:14:20.000Z");
	private static final ZonedDateTime ZONED_PREDICTION_DATE = ZonedDateTime.ofInstant(PREDICTION_DATE, ZoneId.systemDefault());
	private static final ZonedDateTime EVENT_DATE = ZonedDateTime.of(2021, 10, 10, 11, 59, 0, 0, UTC);
	private static final int AMOUNT = 50;
	
	@InjectMocks
	private PredictionsHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private IEventManager eventManager;
	@Mock
	private BetPlacer betPlacer;
	@Mock
	private PredictionMade predictionMade;
	@Mock
	private PredictionMadeData predictionMadeData;
	@Mock
	private Prediction wsPrediction;
	@Mock
	private Topic topic;
	@Mock
	private Streamer streamer;
	@Mock
	private Event event;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		lenient().when(predictionMade.getData()).thenReturn(predictionMadeData);
		lenient().when(predictionMadeData.getPrediction()).thenReturn(wsPrediction);
		lenient().when(wsPrediction.getPoints()).thenReturn(AMOUNT);
		lenient().when(wsPrediction.getEventId()).thenReturn(EVENT_ID);
		lenient().when(wsPrediction.getChannelId()).thenReturn(STREAMER_ID);
		lenient().when(wsPrediction.getOutcomeId()).thenReturn(OUTCOME_ID);
		lenient().when(wsPrediction.getPredictedAt()).thenReturn(ZONED_PREDICTION_DATE);
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getUsername()).thenReturn(CHANNEL_NAME);
	}
	
	@Test
	void noPredictionsMadePreviously(){
		var placedPrediction = PlacedPrediction.builder()
				.eventId(EVENT_ID)
				.amount(AMOUNT)
				.outcomeId(OUTCOME_ID)
				.predictedAt(PREDICTION_DATE)
				.build();
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionMade));
		assertThat(tested.getPlacedPredictions()).containsOnly(Map.entry(EVENT_ID, placedPrediction));
		
		verify(eventManager).onEvent(new PredictionMadeEvent(STREAMER_ID, CHANNEL_NAME, streamer, placedPrediction));
	}
	
	@Test
	void noPredictionsMadePreviouslyUnknownStreamer(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		var placedPrediction = PlacedPrediction.builder()
				.eventId(EVENT_ID)
				.amount(AMOUNT)
				.outcomeId(OUTCOME_ID)
				.predictedAt(PREDICTION_DATE)
				.build();
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionMade));
		assertThat(tested.getPlacedPredictions()).containsOnly(Map.entry(EVENT_ID, placedPrediction));
		
		verify(eventManager).onEvent(new PredictionMadeEvent(STREAMER_ID, null, null, placedPrediction));
	}
	
	@Test
	void predictionsMadePreviously(){
		var prediction = BettingPrediction.builder()
				.event(event)
				.lastUpdate(EVENT_DATE)
				.streamer(streamer)
				.state(PLACED)
				.build();
		var placedPrediction = PlacedPrediction.builder()
				.eventId(EVENT_ID)
				.amount(AMOUNT)
				.outcomeId(OUTCOME_ID)
				.bettingPrediction(prediction)
				.predictedAt(PREDICTION_DATE)
				.build();
		
		tested.getPredictions().put(EVENT_ID, prediction);
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionMade));
		assertThat(tested.getPlacedPredictions()).containsOnly(Map.entry(EVENT_ID, placedPrediction));
		
		verify(eventManager).onEvent(new PredictionMadeEvent(STREAMER_ID, CHANNEL_NAME, streamer, placedPrediction));
	}
	
	@Test
	void predictionsEventFiredTwiceReplacesPrevious(){
		var previousPlaced = mock(PlacedPrediction.class);
		var placedPrediction = PlacedPrediction.builder()
				.eventId(EVENT_ID)
				.amount(AMOUNT)
				.outcomeId(OUTCOME_ID)
				.predictedAt(PREDICTION_DATE)
				.build();
		
		tested.getPlacedPredictions().put(EVENT_ID, previousPlaced);
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionMade));
		assertThat(tested.getPlacedPredictions()).containsOnly(Map.entry(EVENT_ID, placedPrediction));
		
		verify(eventManager).onEvent(new PredictionMadeEvent(STREAMER_ID, CHANNEL_NAME, streamer, placedPrediction));
	}
}