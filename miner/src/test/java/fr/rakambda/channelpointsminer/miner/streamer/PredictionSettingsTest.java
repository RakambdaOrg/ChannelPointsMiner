package fr.rakambda.channelpointsminer.miner.streamer;

import fr.rakambda.channelpointsminer.miner.prediction.bet.action.IPredictionAction;
import fr.rakambda.channelpointsminer.miner.prediction.bet.amount.IAmountCalculator;
import fr.rakambda.channelpointsminer.miner.prediction.bet.outcome.IOutcomePicker;
import fr.rakambda.channelpointsminer.miner.prediction.delay.IDelayCalculator;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class PredictionSettingsTest{
	@Mock
	private IDelayCalculator delayCalculator;
	@Mock
	private IOutcomePicker outcomePicker;
	@Mock
	private IAmountCalculator amountCalculator;
	@Mock
	private IPredictionAction predictionAction;
	
	@Test
	void copy(){
		var actions = List.of(predictionAction);
		var tested = PredictionSettings.builder()
				.delayCalculator(delayCalculator)
				.minimumPointsRequired(25)
				.outcomePicker(outcomePicker)
				.amountCalculator(amountCalculator)
				.actions(actions)
				.build();
		
		var copy = new PredictionSettings(tested);
		
		assertThat(copy.getMinimumPointsRequired()).isEqualTo(tested.getMinimumPointsRequired());
		assertThat(copy.getDelayCalculator()).isSameAs(delayCalculator);
		assertThat(copy.getOutcomePicker()).isSameAs(outcomePicker);
		assertThat(copy.getAmountCalculator()).isSameAs(amountCalculator);
		assertThat(copy.getActions()).isSameAs(actions);
	}
}