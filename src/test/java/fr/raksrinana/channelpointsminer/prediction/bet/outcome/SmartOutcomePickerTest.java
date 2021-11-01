package fr.raksrinana.twitchminer.prediction.bet.outcome;

import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.OutcomeColor;
import fr.raksrinana.twitchminer.handler.data.Prediction;
import fr.raksrinana.twitchminer.prediction.bet.BetPlacementException;
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

@ExtendWith(MockitoExtension.class)
class SmartOutcomePickerTest{
	private final SmartOutcomePicker tested = SmartOutcomePicker.builder().percentageGap(.1F).build();
	
	@Mock
	private Prediction prediction;
	@Mock
	private Event event;
	@Mock
	private Outcome blueOutcome;
	@Mock
	private Outcome pinkOutcome;
	
	@BeforeEach
	void setUp(){
		lenient().when(prediction.getEvent()).thenReturn(event);
		lenient().when(event.getOutcomes()).thenReturn(List.of(blueOutcome, pinkOutcome));
		
		lenient().when(blueOutcome.getColor()).thenReturn(OutcomeColor.BLUE);
		lenient().when(pinkOutcome.getColor()).thenReturn(OutcomeColor.PINK);
	}
	
	@Test
	void choseByPoints() throws BetPlacementException{
		when(blueOutcome.getTotalUsers()).thenReturn(49);
		when(pinkOutcome.getTotalUsers()).thenReturn(51);
		
		when(blueOutcome.getTotalPoints()).thenReturn(10L);
		when(pinkOutcome.getTotalPoints()).thenReturn(20L);
		
		assertThat(tested.chooseOutcome(prediction)).isEqualTo(blueOutcome);
	}
	
	@Test
	void choseByUsers() throws BetPlacementException{
		when(blueOutcome.getTotalUsers()).thenReturn(40);
		when(pinkOutcome.getTotalUsers()).thenReturn(60);
		
		assertThat(tested.chooseOutcome(prediction)).isEqualTo(pinkOutcome);
	}
	
	@Test
	void missingBlue(){
		when(event.getOutcomes()).thenReturn(List.of(pinkOutcome));
		
		assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(prediction));
	}
	
	@Test
	void missingPink(){
		when(event.getOutcomes()).thenReturn(List.of(blueOutcome));
		
		assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(prediction));
	}
}