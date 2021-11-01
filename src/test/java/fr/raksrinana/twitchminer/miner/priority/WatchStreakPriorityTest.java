package fr.raksrinana.twitchminer.miner.priority;

import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchStreakPriorityTest{
	private static final int SCORE = 50;
	
	private final WatchStreakPriority tested = WatchStreakPriority.builder().score(SCORE).build();
	
	@Mock
	private Streamer streamer;
	@Mock
	private IMiner miner;
	
	@Test
	void getScoreMayClaim(){
		when(streamer.mayClaimStreak()).thenReturn(true);
		assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
	}
	
	@Test
	void getScoreMayNotClaim(){
		when(streamer.mayClaimStreak()).thenReturn(false);
		assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
	}
}