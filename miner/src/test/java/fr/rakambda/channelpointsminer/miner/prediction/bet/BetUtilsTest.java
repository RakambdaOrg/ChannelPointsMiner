package fr.rakambda.channelpointsminer.miner.prediction.bet;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class BetUtilsTest{
	@Mock
	private Outcome outcome;
	@Mock
	private Outcome outcome2;
	
	@Test
	void test(){
		when(outcome.getTotalUsers()).thenReturn(150);
		when(outcome.getTotalPoints()).thenReturn(50L);
		when(outcome2.getTotalUsers()).thenReturn(50);
		when(outcome2.getTotalPoints()).thenReturn(100L);
		
		assertThat(BetUtils.getKellyValue(outcome, outcome2)).isEqualTo(0.625f);
	}
	
	@Test
	void test2(){
		when(outcome.getTotalUsers()).thenReturn(50);
		when(outcome.getTotalPoints()).thenReturn(100L);
		when(outcome2.getTotalUsers()).thenReturn(150);
		when(outcome2.getTotalPoints()).thenReturn(50L);
		
		assertThat(BetUtils.getKellyValue(outcome, outcome2)).isEqualTo(-1.25f);
	}
	
	@Test
	void testVeryLowGain(){
		when(outcome.getTotalUsers()).thenReturn(50);
		when(outcome.getTotalPoints()).thenReturn(100L);
		when(outcome2.getTotalUsers()).thenReturn(150);
		when(outcome2.getTotalPoints()).thenReturn(0L);
		
		assertThat(BetUtils.getKellyValue(outcome, outcome2)).isEqualTo(0f);
	}
	
	@Test
	void testNoUsers(){
		when(outcome.getTotalUsers()).thenReturn(0);
		when(outcome2.getTotalUsers()).thenReturn(0);
		
		assertThat(BetUtils.getKellyValue(outcome, outcome2)).isEqualTo(0);
	}
}