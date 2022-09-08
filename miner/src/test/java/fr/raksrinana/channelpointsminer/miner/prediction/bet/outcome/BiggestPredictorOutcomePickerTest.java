package fr.raksrinana.channelpointsminer.miner.prediction.bet.outcome;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Predictor;
import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class BiggestPredictorOutcomePickerTest{
	private final BiggestPredictorOutcomePicker tested = BiggestPredictorOutcomePicker.builder().build();
	
	@Mock
	private IDatabase database;
	@Mock
	private BettingPrediction bettingPrediction;
	@Mock
	private Event event;
	@Mock
	private Outcome blueOutcome;
	@Mock
	private Outcome pinkOutcome;
	
	@BeforeEach
	void setUp(){
		lenient().when(bettingPrediction.getEvent()).thenReturn(event);
		lenient().when(event.getOutcomes()).thenReturn(List.of(blueOutcome, pinkOutcome));
	}
	
	@Test
	void chose() throws BetPlacementException{
		var predictor10 = mock(Predictor.class);
		var predictor11 = mock(Predictor.class);
		var predictor20 = mock(Predictor.class);
		var predictor21 = mock(Predictor.class);
		
		when(predictor10.getPoints()).thenReturn(20);
		when(predictor11.getPoints()).thenReturn(30);
		when(predictor20.getPoints()).thenReturn(25);
		when(predictor21.getPoints()).thenReturn(35);
		
		when(blueOutcome.getTopPredictors()).thenReturn(List.of(predictor10, predictor11));
		when(pinkOutcome.getTopPredictors()).thenReturn(List.of(predictor20, predictor21));
		
		assertThat(tested.chooseOutcome(bettingPrediction, database)).isEqualTo(pinkOutcome);
	}
	
	@Test
	void missingOutcome(){
		when(event.getOutcomes()).thenReturn(List.of());
		
		assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(bettingPrediction, database));
	}
}