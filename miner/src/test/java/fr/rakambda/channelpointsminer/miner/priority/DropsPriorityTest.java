package fr.rakambda.channelpointsminer.miner.priority;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropchannelcampaignsprogress.DropChannelCampaignsProgressData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsCampaign;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsProgressCriteria;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsRewardGroup;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsRewardGroupSelfEdge;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.ZonedDateTime;
import java.util.List;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class DropsPriorityTest{
	private static final int SCORE = 50;
	private static final ZonedDateTime NOW = ZonedDateTime.of(2021, 10, 10, 12, 0, 0, 0, UTC);
	
	private final DropsPriority tested = DropsPriority.builder().score(SCORE).build();
	
	@Mock
	private Streamer streamer;
	@Mock
	private IMiner miner;
	@Mock
	private DropChannelCampaignsProgressData dropChannelCampaignsProgressData;
	@Mock
	private DropsCampaign dropsCampaign;
	@Mock
	private DropsRewardGroup dropsRewardGroup;
	@Mock
	private DropsProgressCriteria dropsProgressCriteria;
	@Mock
	private DropsRewardGroupSelfEdge dropsRewardGroupSelfEdge;
	
	@BeforeEach
	void setUp(){
		lenient().when(streamer.isParticipateCampaigns()).thenReturn(true);
		lenient().when(streamer.isStreamingGame()).thenReturn(true);
		lenient().when(streamer.isExcludeSubscriberDrops()).thenReturn(true);
		
		lenient().when(streamer.getDropChannelCampaignsProgress()).thenReturn(dropChannelCampaignsProgressData);
		lenient().when(dropChannelCampaignsProgressData.getChannelDropCampaignsProgress()).thenReturn(List.of(dropsCampaign));
		
		lenient().when(dropsCampaign.getStartAt()).thenReturn(NOW.minusHours(1));
		lenient().when(dropsCampaign.getEndAt()).thenReturn(NOW.plusHours(1));
		lenient().when(dropsCampaign.getRewardGroups()).thenReturn(List.of(dropsRewardGroup));
		
		lenient().when(dropsRewardGroup.getProgressCriteria()).thenReturn(dropsProgressCriteria);
		lenient().when(dropsRewardGroup.getSelf()).thenReturn(dropsRewardGroupSelfEdge);
		
		lenient().when(dropsProgressCriteria.getRequirementType()).thenReturn("WATCH");
		lenient().when(dropsRewardGroupSelfEdge.getStatus()).thenReturn("IN_PROGRESS");
	}
	
	@Test
	void notParticipatingCampaigns(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(streamer.isParticipateCampaigns()).thenReturn(false);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void notStreamingGame(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(streamer.isStreamingGame()).thenReturn(false);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void noDropsProgress(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(streamer.getDropChannelCampaignsProgress()).thenReturn(null);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void noDropCampaigns(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropChannelCampaignsProgressData.getChannelDropCampaignsProgress()).thenReturn(List.of());
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void tooEarly(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropsCampaign.getStartAt()).thenReturn(NOW.plusSeconds(1));
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void tooLate(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropsCampaign.getEndAt()).thenReturn(NOW.minusSeconds(1));
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void requiresSubscription(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropsProgressCriteria.getRequirementType()).thenReturn("SUB");
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void valid(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
		}
	}
	
	@Test
	void validWithSubscriptionRequired(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(streamer.isExcludeSubscriberDrops()).thenReturn(false);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
		}
	}
	
	@Test
	void validNoCampaignStartDate(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropsCampaign.getStartAt()).thenReturn(null);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
		}
	}
	
	@Test
	void validNoCampaignEndDate(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropsCampaign.getEndAt()).thenReturn(null);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
		}
	}

	@Test
	void noRewardGroups(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropsCampaign.getRewardGroups()).thenReturn(List.of());
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void rewardGroupNotInProgress(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropsRewardGroupSelfEdge.getStatus()).thenReturn("CLAIMED");
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
}