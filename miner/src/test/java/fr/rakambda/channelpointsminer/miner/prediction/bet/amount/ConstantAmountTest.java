package fr.rakambda.channelpointsminer.miner.prediction.bet.amount;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class ConstantAmountTest{
	private static final int AMOUNT = 50;
	
	private final ConstantAmount tested = ConstantAmount.builder().amount(AMOUNT).build();
	
	@Mock
	private BettingPrediction bettingPrediction;
	@Mock
	private Outcome outcome;
	
	@Test
	void calculate(){
		assertThat(tested.calculateAmount(bettingPrediction, outcome)).isEqualTo(AMOUNT);
	}
}
