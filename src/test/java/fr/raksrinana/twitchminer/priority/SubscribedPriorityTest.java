package fr.raksrinana.twitchminer.priority;

import fr.raksrinana.twitchminer.api.gql.data.types.CommunityPointsMultiplier;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.streamer.Streamer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static fr.raksrinana.twitchminer.api.gql.data.types.MultiplierReasonCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscribedPriorityTest{
	private static final int SCORE_T1 = 50;
	private static final int SCORE_T2 = 51;
	private static final int SCORE_T3 = 52;
	
	private final SubscribedPriority tested = SubscribedPriority.builder()
			.score(SCORE_T1)
			.score2(SCORE_T2)
			.score3(SCORE_T3)
			.build();
	
	@Mock
	private Streamer streamer;
	@Mock
	private IMiner miner;
	@Mock
	private CommunityPointsMultiplier t1Multiplier;
	@Mock
	private CommunityPointsMultiplier t2Multiplier;
	@Mock
	private CommunityPointsMultiplier t3Multiplier;
	
	@BeforeEach
	void setUp(){
		lenient().when(t1Multiplier.getReasonCode()).thenReturn(SUB_T1);
		lenient().when(t2Multiplier.getReasonCode()).thenReturn(SUB_T2);
		lenient().when(t3Multiplier.getReasonCode()).thenReturn(SUB_T3);
	}
	
	@Test
	void getScoreNoMultipliers(){
		when(streamer.getActiveMultipliers()).thenReturn(List.of());
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
	}
	
	@Test
	void getScoreT1(){
		when(streamer.getActiveMultipliers()).thenReturn(List.of(t1Multiplier));
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(50);
	}
	
	@Test
	void getScoreT2(){
		when(streamer.getActiveMultipliers()).thenReturn(List.of(t2Multiplier));
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(51);
	}
	
	@Test
	void getScoreT1T2(){
		when(streamer.getActiveMultipliers()).thenReturn(List.of(t2Multiplier, t1Multiplier));
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(51);
	}
	
	@Test
	void getScoreT3(){
		when(streamer.getActiveMultipliers()).thenReturn(List.of(t3Multiplier));
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(52);
	}
	
	@Test
	void getScoreT3T2(){
		when(streamer.getActiveMultipliers()).thenReturn(List.of(t3Multiplier, t2Multiplier));
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(52);
	}
	
	@Test
	void getScoreT3T1(){
		when(streamer.getActiveMultipliers()).thenReturn(List.of(t3Multiplier, t1Multiplier));
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(52);
	}
	
	@Test
	void getScoreT3T2T1(){
		when(streamer.getActiveMultipliers()).thenReturn(List.of(t3Multiplier, t2Multiplier, t1Multiplier));
		
		assertThat(tested.getScore(miner, streamer)).isEqualTo(52);
	}
}