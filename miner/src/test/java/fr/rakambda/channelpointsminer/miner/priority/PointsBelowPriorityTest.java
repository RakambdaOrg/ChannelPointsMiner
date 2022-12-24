package fr.rakambda.channelpointsminer.miner.priority;

import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class PointsBelowPriorityTest{
	private static final int SCORE = 50;
	private static final int THRESHOLD = 100;
	
	private final PointsBelowPriority tested = PointsBelowPriority.builder()
			.score(SCORE)
			.threshold(THRESHOLD)
			.build();
	
	@Mock
	private Streamer streamer;
	@Mock
	private IMiner miner;
	
	@Test
	void getScoreAboveThreshold(){
		when(streamer.getChannelPoints()).thenReturn(Optional.of(THRESHOLD + 1));
		
		Assertions.assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
	}
	
	@Test
	void getScoreBelowThreshold(){
		when(streamer.getChannelPoints()).thenReturn(Optional.of(THRESHOLD - 1));
		
		Assertions.assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
	}
	
	@Test
	void getScoreEqualThreshold(){
		when(streamer.getChannelPoints()).thenReturn(Optional.of(THRESHOLD));
		
		Assertions.assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
	}
	
	@Test
	void getScoreNoPointsData(){
		when(streamer.getChannelPoints()).thenReturn(Optional.empty());
		
		Assertions.assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
	}
}