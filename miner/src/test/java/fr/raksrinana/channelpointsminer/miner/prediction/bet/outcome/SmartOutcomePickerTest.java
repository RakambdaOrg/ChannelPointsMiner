package fr.raksrinana.channelpointsminer.miner.prediction.bet.outcome;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.OutcomeColor;
import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.exception.NotEnoughUsersBetPlacementException;
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
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class SmartOutcomePickerTest{
	private final SmartOutcomePicker tested = SmartOutcomePicker.builder().percentageGap(.1F).build();
	
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
	@Mock
	private Outcome redOutcome;
	
	@BeforeEach
	void setUp(){
		lenient().when(bettingPrediction.getEvent()).thenReturn(event);
		lenient().when(event.getOutcomes()).thenReturn(List.of(blueOutcome, pinkOutcome));
		
		lenient().when(blueOutcome.getColor()).thenReturn(OutcomeColor.BLUE);
		lenient().when(pinkOutcome.getColor()).thenReturn(OutcomeColor.PINK);
	}
	
	@Test
	void choseByPoints() throws BetPlacementException{
		when(blueOutcome.getTotalUsers()).thenReturn(52);
		when(pinkOutcome.getTotalUsers()).thenReturn(51);
		
		when(blueOutcome.getTotalPoints()).thenReturn(10L);
		when(pinkOutcome.getTotalPoints()).thenReturn(20L);
		
		assertThat(tested.chooseOutcome(bettingPrediction, database)).isEqualTo(blueOutcome);
	}
	
	@Test
	void choseByPointsMoreOutcomes() throws BetPlacementException{
		when(event.getOutcomes()).thenReturn(List.of(blueOutcome, pinkOutcome, redOutcome));
		
		when(blueOutcome.getTotalUsers()).thenReturn(49);
		when(pinkOutcome.getTotalUsers()).thenReturn(51);
		when(redOutcome.getTotalUsers()).thenReturn(52);
		
		when(pinkOutcome.getTotalPoints()).thenReturn(20L);
		when(redOutcome.getTotalPoints()).thenReturn(19L);
		
		assertThat(tested.chooseOutcome(bettingPrediction, database)).isEqualTo(redOutcome);
	}
	
	@Test
	void choseByUsers() throws BetPlacementException{
		when(blueOutcome.getTotalUsers()).thenReturn(40);
		when(pinkOutcome.getTotalUsers()).thenReturn(60);
		
		assertThat(tested.chooseOutcome(bettingPrediction, database)).isEqualTo(pinkOutcome);
	}
	
	@Test
	void missingBlue(){
		when(event.getOutcomes()).thenReturn(List.of(pinkOutcome));
		when(pinkOutcome.getTotalUsers()).thenReturn(1);
		
		assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(bettingPrediction, database));
	}
	
	@Test
	void missingPink(){
		when(event.getOutcomes()).thenReturn(List.of(blueOutcome));
		when(blueOutcome.getTotalUsers()).thenReturn(1);
		
		assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(bettingPrediction, database));
	}
	
	@Test
	void noUsers() throws BetPlacementException{
		when(blueOutcome.getTotalUsers()).thenReturn(0);
		when(pinkOutcome.getTotalUsers()).thenReturn(0);
		
		assertThrows(NotEnoughUsersBetPlacementException.class, () -> tested.chooseOutcome(bettingPrediction, database));
	}
}