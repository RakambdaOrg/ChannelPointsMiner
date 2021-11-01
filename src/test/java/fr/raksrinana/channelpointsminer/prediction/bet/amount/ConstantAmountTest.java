package fr.raksrinana.twitchminer.prediction.bet.amount;

import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.twitchminer.handler.data.Prediction;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ConstantAmountTest{
	private static final int AMOUNT = 50;
	
	private final ConstantAmount tested = ConstantAmount.builder().amount(AMOUNT).build();
	
	@Mock
	private Prediction prediction;
	@Mock
	private Outcome outcome;
	
	@Test
	void calculate(){
		assertThat(tested.calculateAmount(prediction, outcome)).isEqualTo(AMOUNT);
	}
}
