package fr.raksrinana.channelpointsminer.miner.prediction.bet.action;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Predictor;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.BetPlacementException;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.Placement;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StealthPredictionActionTest{
	private static final int AMOUNT = 50;
	
	private final StealthPredictionAction tested = StealthPredictionAction.builder().build();
	
	@Mock
	private BettingPrediction bettingPrediction;
	@Mock
	private Outcome outcome;
	
	private Placement placement;
	
	@BeforeEach
	void setUp(){
		placement = Placement.builder()
				.bettingPrediction(bettingPrediction)
				.outcome(outcome)
				.amount(AMOUNT)
				.build();
	}
	
	@Test
	void noOneElsePlacedABetOnOutcome(){
		when(outcome.getTopPredictors()).thenReturn(List.of());
		
		assertThrows(BetPlacementException.class, () -> tested.perform(placement));
		
		assertThat(placement).isEqualTo(Placement.builder()
				.bettingPrediction(bettingPrediction)
				.outcome(outcome)
				.amount(AMOUNT)
				.build());
	}
	
	@Test
	void predictionIsLowerThanTopPredictor() throws BetPlacementException{
		var predictor1 = mock(Predictor.class);
		var predictor2 = mock(Predictor.class);
		
		when(predictor1.getPoints()).thenReturn(AMOUNT + 10);
		when(predictor2.getPoints()).thenReturn(AMOUNT - 10);
		
		when(outcome.getTopPredictors()).thenReturn(List.of(predictor1, predictor2));
		
		tested.perform(placement);
		
		assertThat(placement).isEqualTo(Placement.builder()
				.bettingPrediction(bettingPrediction)
				.outcome(outcome)
				.amount(AMOUNT)
				.build());
	}
	
	@Test
	void predictionIsHigherThanTopPredictor() throws BetPlacementException{
		var predictor1 = mock(Predictor.class);
		var predictor2 = mock(Predictor.class);
		
		when(predictor1.getPoints()).thenReturn(AMOUNT - 5);
		when(predictor2.getPoints()).thenReturn(AMOUNT - 10);
		
		when(outcome.getTopPredictors()).thenReturn(List.of(predictor1, predictor2));
		
		tested.perform(placement);
		
		assertThat(placement).isEqualTo(Placement.builder()
				.bettingPrediction(bettingPrediction)
				.outcome(outcome)
				.amount(AMOUNT - 5 - 1)
				.build());
	}
	
	@Test
	void predictionIsSameAsTopPredictor() throws BetPlacementException{
		var predictor1 = mock(Predictor.class);
		var predictor2 = mock(Predictor.class);
		
		when(predictor1.getPoints()).thenReturn(AMOUNT);
		when(predictor2.getPoints()).thenReturn(AMOUNT - 10);
		
		when(outcome.getTopPredictors()).thenReturn(List.of(predictor1, predictor2));
		
		tested.perform(placement);
		
		assertThat(placement).isEqualTo(Placement.builder()
				.bettingPrediction(bettingPrediction)
				.outcome(outcome)
				.amount(AMOUNT - 1)
				.build());
	}
}