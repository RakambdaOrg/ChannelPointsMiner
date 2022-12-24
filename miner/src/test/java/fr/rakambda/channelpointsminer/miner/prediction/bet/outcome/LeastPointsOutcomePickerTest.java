package fr.rakambda.channelpointsminer.miner.prediction.bet.outcome;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.database.IDatabase;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class LeastPointsOutcomePickerTest{
	private final LeastPointsOutcomePicker tested = LeastPointsOutcomePicker.builder().build();
	
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
		when(blueOutcome.getTotalPoints()).thenReturn(20L);
		when(pinkOutcome.getTotalPoints()).thenReturn(19L);
		
		assertThat(tested.chooseOutcome(bettingPrediction, database)).isEqualTo(pinkOutcome);
	}
	
	@Test
	void missingOutcome(){
		when(event.getOutcomes()).thenReturn(List.of());
		
		assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(bettingPrediction, database));
	}
}