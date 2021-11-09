package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.PredictionUpdated;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionupdated.PredictionUpdatedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.handler.data.PlacedPrediction;
import fr.raksrinana.channelpointsminer.handler.data.Prediction;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacer;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import static fr.raksrinana.channelpointsminer.handler.data.PredictionState.PLACED;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PredictionsHandlerPredictionUpdatedTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String EVENT_ID = "event-id";
	private static final ZonedDateTime EVENT_DATE = ZonedDateTime.of(2021, 10, 10, 11, 59, 0, 0, UTC);
	private static final int AMOUNT = 50;
	
	@InjectMocks
	private PredictionsHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private BetPlacer betPlacer;
	@Mock
	private PredictionUpdated predictionUpdated;
	@Mock
	private PredictionUpdatedData predictionUpdatedData;
	@Mock
	private fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Prediction wsPrediction;
	@Mock
	private Topic topic;
	@Mock
	private Streamer streamer;
	@Mock
	private Event event;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		lenient().when(predictionUpdated.getData()).thenReturn(predictionUpdatedData);
		lenient().when(predictionUpdatedData.getPrediction()).thenReturn(wsPrediction);
		lenient().when(wsPrediction.getPoints()).thenReturn(AMOUNT);
		lenient().when(wsPrediction.getEventId()).thenReturn(EVENT_ID);
		lenient().when(wsPrediction.getChannelId()).thenReturn(STREAMER_ID);
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void noPredictionsMadePreviously(){
		assertDoesNotThrow(() -> tested.handle(topic, predictionUpdated));
		assertThat(tested.getPlacedPredictions()).containsOnly(Map.entry(EVENT_ID, PlacedPrediction.builder()
				.eventId(EVENT_ID)
				.amount(AMOUNT)
				.build()));
	}
	
	@Test
	void predictionsMadePreviously(){
		var prediction = Prediction.builder()
				.event(event)
				.lastUpdate(EVENT_DATE)
				.streamer(streamer)
				.state(PLACED)
				.build();
		tested.getPredictions().put(EVENT_ID, prediction);
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionUpdated));
		assertThat(tested.getPlacedPredictions()).containsOnly(Map.entry(EVENT_ID, PlacedPrediction.builder()
				.eventId(EVENT_ID)
				.amount(AMOUNT)
				.prediction(prediction)
				.build()));
	}
	
	@Test
	void predictionsEventFiredTwiceReplacesPrevious(){
		var previousPlaced = mock(PlacedPrediction.class);
		tested.getPlacedPredictions().put(EVENT_ID, previousPlaced);
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionUpdated));
		assertThat(tested.getPlacedPredictions()).containsOnly(Map.entry(EVENT_ID, PlacedPrediction.builder()
				.eventId(EVENT_ID)
				.amount(AMOUNT)
				.build()));
	}
}