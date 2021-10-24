package fr.raksrinana.twitchminer.miner.priority;

import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointsAbovePriorityTest{
	private static final int SCORE = 50;
	private static final int THRESHOLD = 100;
	
	private final PointsAbovePriority tested = PointsAbovePriority.builder()
			.score(SCORE)
			.threshold(THRESHOLD)
			.build();
	
	@Mock
	private Streamer streamer;
	
	@Test
	void getScoreAboveThreshold(){
		when(streamer.getChannelPoints()).thenReturn(Optional.of(THRESHOLD + 1));
		
		assertThat(tested.getScore(streamer)).isEqualTo(SCORE);
	}
	
	@Test
	void getScoreBelowThreshold(){
		when(streamer.getChannelPoints()).thenReturn(Optional.of(THRESHOLD - 1));
		
		assertThat(tested.getScore(streamer)).isEqualTo(0);
	}
	
	@Test
	void getScoreEqualThreshold(){
		when(streamer.getChannelPoints()).thenReturn(Optional.of(THRESHOLD));
		
		assertThat(tested.getScore(streamer)).isEqualTo(0);
	}
	
	@Test
	void getScoreNoPointsData(){
		when(streamer.getChannelPoints()).thenReturn(Optional.empty());
		
		assertThat(tested.getScore(streamer)).isEqualTo(0);
	}
}