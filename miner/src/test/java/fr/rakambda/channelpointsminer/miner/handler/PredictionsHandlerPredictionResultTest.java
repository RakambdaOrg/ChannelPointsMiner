package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PredictionResult;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.predictionresult.PredictionResultData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Prediction;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.PredictionResultPayload;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.PredictionResultType;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionResultEvent;
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
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class PredictionsHandlerPredictionResultTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String CHANNEL_NAME = "channel-name";
	private static final String EVENT_ID = "event-id";
	private static final Instant RESULT_DATE = Instant.parse("2020-05-17T12:14:20.000Z");
	private static final ZonedDateTime ZONED_RESULT_DATE = ZonedDateTime.ofInstant(RESULT_DATE, ZoneId.systemDefault());
	
	@InjectMocks
	private PredictionsHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private IEventManager eventManager;
	@Mock
	private BetPlacer betPlacer;
	@Mock
	private PredictionResult predictionResult;
	@Mock
	private PredictionResultData predictionResultData;
	@Mock
	private Prediction wsPrediction;
	@Mock
	private PredictionResultPayload predictionResultPayload;
	@Mock
	private Topic topic;
	@Mock
	private Streamer streamer;
	
	@BeforeEach
	void setUp(){
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		lenient().when(predictionResult.getData()).thenReturn(predictionResultData);
		lenient().when(predictionResultData.getPrediction()).thenReturn(wsPrediction);
		lenient().when(predictionResultData.getTimestamp()).thenReturn(ZONED_RESULT_DATE);
		lenient().when(wsPrediction.getEventId()).thenReturn(EVENT_ID);
		lenient().when(wsPrediction.getChannelId()).thenReturn(STREAMER_ID);
		lenient().when(wsPrediction.getResult()).thenReturn(predictionResultPayload);
		lenient().when(predictionResultPayload.getType()).thenReturn(PredictionResultType.WIN);
		lenient().when(predictionResultPayload.getPointsWon()).thenReturn(100);
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getUsername()).thenReturn(CHANNEL_NAME);
	}
	
	@Test
	void noResult(){
		var prediction = mock(BettingPrediction.class);
		var predictionPlaced = mock(PlacedPrediction.class);
		tested.getPredictions().put(EVENT_ID, prediction);
		tested.getPlacedPredictions().put(EVENT_ID, predictionPlaced);
		
		when(wsPrediction.getResult()).thenReturn(null);
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionResult));
		assertThat(tested.getPlacedPredictions()).isNotEmpty();
		assertThat(tested.getPredictions()).isNotEmpty();
		
		verify(eventManager, never()).onEvent(any());
	}
	
	@Test
	void noPredictionPlacedData(){
		var prediction = mock(BettingPrediction.class);
		tested.getPredictions().put(EVENT_ID, prediction);
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionResult));
		assertThat(tested.getPredictions()).isEmpty();
		
		verify(eventManager).onEvent(new PredictionResultEvent(STREAMER_ID, CHANNEL_NAME, streamer, null, predictionResultData));
	}
	
	@Test
	void removedPredictionPlaced(){
		var prediction = mock(BettingPrediction.class);
		var predictionPlaced = mock(PlacedPrediction.class);
		
		tested.getPredictions().put(EVENT_ID, prediction);
		tested.getPlacedPredictions().put(EVENT_ID, predictionPlaced);
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionResult));
		assertThat(tested.getPlacedPredictions()).isEmpty();
		assertThat(tested.getPredictions()).isEmpty();
		
		verify(eventManager).onEvent(new PredictionResultEvent(STREAMER_ID, CHANNEL_NAME, streamer, predictionPlaced, predictionResultData));
	}
	
	@Test
	void removedPredictionPlacedUnknownStreamer(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		var prediction = mock(BettingPrediction.class);
		var predictionPlaced = mock(PlacedPrediction.class);
		
		tested.getPredictions().put(EVENT_ID, prediction);
		tested.getPlacedPredictions().put(EVENT_ID, predictionPlaced);
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionResult));
		assertThat(tested.getPlacedPredictions()).isEmpty();
		assertThat(tested.getPredictions()).isEmpty();
		
		verify(eventManager).onEvent(new PredictionResultEvent(STREAMER_ID, null, null, predictionPlaced, predictionResultData));
	}
}