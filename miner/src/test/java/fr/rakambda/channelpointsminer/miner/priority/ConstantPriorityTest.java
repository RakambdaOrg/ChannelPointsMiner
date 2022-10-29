package fr.rakambda.channelpointsminer.miner.priority;

import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class ConstantPriorityTest{
	private static final int SCORE = 50;
	
	private final ConstantPriority tested = ConstantPriority.builder().score(SCORE).build();
	
	@Mock
	private Streamer streamer;
	@Mock
	private IMiner miner;
	
	@Test
	void getScore(){
		assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
	}
}