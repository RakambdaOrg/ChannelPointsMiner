package fr.raksrinana.channelpointsminer.streamer;

import fr.raksrinana.channelpointsminer.prediction.bet.amount.AmountCalculator;
import fr.raksrinana.channelpointsminer.prediction.bet.outcome.OutcomePicker;
import fr.raksrinana.channelpointsminer.prediction.delay.DelayCalculator;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PredictionSettingsTest{
	@Mock
	private DelayCalculator delayCalculator;
	@Mock
	private OutcomePicker outcomePicker;
	@Mock
	private AmountCalculator amountCalculator;
	
	@Test
	void copy(){
		var tested = PredictionSettings.builder()
				.delayCalculator(delayCalculator)
				.minimumPointsRequired(25)
				.outcomePicker(outcomePicker)
				.amountCalculator(amountCalculator)
				.build();
		
		var copy = new PredictionSettings(tested);
		
		assertThat(copy.getMinimumPointsRequired()).isEqualTo(tested.getMinimumPointsRequired());
		assertThat(copy.getDelayCalculator()).isSameAs(delayCalculator);
		assertThat(copy.getOutcomePicker()).isSameAs(outcomePicker);
		assertThat(copy.getAmountCalculator()).isSameAs(amountCalculator);
	}
}