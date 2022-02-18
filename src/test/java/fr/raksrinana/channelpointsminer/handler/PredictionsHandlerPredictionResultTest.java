package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.PredictionResult;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionresult.PredictionResultData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PredictionResultPayload;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PredictionResultType;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.event.impl.PredictionResultEvent;
import fr.raksrinana.channelpointsminer.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.handler.data.PlacedPrediction;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacer;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredictionsHandlerPredictionResultTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String CHANNEL_NAME = "channel-name";
	private static final String EVENT_ID = "event-id";
	
	@InjectMocks
	private PredictionsHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private BetPlacer betPlacer;
	@Mock
	private PredictionResult predictionResult;
	@Mock
	private PredictionResultData predictionResultData;
	@Mock
	private fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Prediction wsPrediction;
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
		
		verify(miner, never()).onEvent(any());
	}
	
	@Test
	void noPredictionPlacedData(){
		var prediction = mock(BettingPrediction.class);
		tested.getPredictions().put(EVENT_ID, prediction);
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionResult));
		assertThat(tested.getPredictions()).isEmpty();
		
		verify(miner).onEvent(new PredictionResultEvent(miner, STREAMER_ID, CHANNEL_NAME, streamer, null, predictionResultData));
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
		
		verify(miner).onEvent(new PredictionResultEvent(miner, STREAMER_ID, CHANNEL_NAME, streamer, predictionPlaced, predictionResultData));
	}
}