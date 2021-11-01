package fr.raksrinana.twitchminer.prediction.bet.outcome;

import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Outcome;
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
class MostUsersOutcomePickerTest{
	private final MostUsersOutcomePicker tested = new MostUsersOutcomePicker();
	
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
	}
	
	@Test
	void chose() throws BetPlacementException{
		when(blueOutcome.getTotalUsers()).thenReturn(19);
		when(pinkOutcome.getTotalUsers()).thenReturn(20);
		
		assertThat(tested.chooseOutcome(prediction)).isEqualTo(pinkOutcome);
	}
	
	@Test
	void missingOutcome(){
		when(event.getOutcomes()).thenReturn(List.of());
		
		assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(prediction));
	}
}