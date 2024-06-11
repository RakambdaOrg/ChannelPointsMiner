package fr.rakambda.channelpointsminer.miner.priority;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.inventory.InventoryData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Channel;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropBenefit;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropBenefitEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaign;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaignSummary;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Inventory;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDrop;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.UserDropReward;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.miner.MinerData;
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
	private static final String DROP_ID = "c2542d6d-cd10-4532-919b-3d19f30a768c";
	private static final ZonedDateTime NOW = ZonedDateTime.of(2021, 10, 10, 12, 0, 0, 0, UTC);
	private static final int DROP_CLAIM_LIMIT = 2;
	
	private final DropsPriority tested = DropsPriority.builder().score(SCORE).build();
	
	@Mock
	private Streamer streamer;
	@Mock
	private IMiner miner;
	@Mock
	private DropsHighlightServiceAvailableDropsData dropsHighlightServiceAvailableDropsData;
	@Mock
	private Channel channel;
	@Mock
	private DropCampaign dropCampaign;
	@Mock
	private TimeBasedDrop timeBasedDrop;
	@Mock
	private DropBenefitEdge dropBenefitEdge;
	@Mock
	private DropCampaignSummary dropCampaignSummary;
	@Mock
	private MinerData minerData;
	@Mock
	private InventoryData inventoryData;
	@Mock
	private UserDropReward userDropReward;
	@Mock
	private User user;
	@Mock
	private Inventory inventory;
	@Mock
	private DropBenefit dropBenefit;
	
	@BeforeEach
	void setUp(){
		lenient().when(streamer.isParticipateCampaigns()).thenReturn(true);
		lenient().when(streamer.isStreamingGame()).thenReturn(true);
		lenient().when(streamer.isExcludeSubscriberDrops()).thenReturn(true);
		
		lenient().when(streamer.getDropsHighlightServiceAvailableDrops()).thenReturn(dropsHighlightServiceAvailableDropsData);
		lenient().when(dropsHighlightServiceAvailableDropsData.getChannel()).thenReturn(channel);
		lenient().when(channel.getViewerDropCampaigns()).thenReturn(List.of(dropCampaign));
		
		lenient().when(dropCampaign.getStartAt()).thenReturn(NOW.minusHours(1));
		lenient().when(dropCampaign.getEndAt()).thenReturn(NOW.plusHours(1));
		lenient().when(dropCampaign.getTimeBasedDrops()).thenReturn(List.of(timeBasedDrop));
		lenient().when(dropCampaign.getSummary()).thenReturn(dropCampaignSummary);
		
		lenient().when(dropCampaignSummary.isIncludesSubRequirement()).thenReturn(false);
		
		lenient().when(timeBasedDrop.getStartAt()).thenReturn(NOW.minusMinutes(30));
		lenient().when(timeBasedDrop.getEndAt()).thenReturn(NOW.plusMinutes(30));
		lenient().when(timeBasedDrop.getBenefitEdges()).thenReturn(List.of(dropBenefitEdge));
		
		lenient().when(dropBenefitEdge.getEntitlementLimit()).thenReturn(DROP_CLAIM_LIMIT);
		lenient().when(dropBenefitEdge.getClaimCount()).thenReturn(1);
		lenient().when(dropBenefitEdge.getBenefit()).thenReturn(dropBenefit);
		
		lenient().when(dropBenefit.getId()).thenReturn(DROP_ID);
		
		lenient().when(miner.getMinerData()).thenReturn(minerData);
		lenient().when(minerData.getInventory()).thenReturn(inventoryData);
		lenient().when(inventoryData.getCurrentUser()).thenReturn(user);
		lenient().when(user.getInventory()).thenReturn(inventory);
		lenient().when(inventory.getGameEventDrops()).thenReturn(List.of(userDropReward));
		
		lenient().when(userDropReward.getId()).thenReturn(DROP_ID);
		lenient().when(userDropReward.getTotalCount()).thenReturn(0);
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
	void noDropsHighlights(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(streamer.getDropsHighlightServiceAvailableDrops()).thenReturn(null);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void noDropCampaigns(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(channel.getViewerDropCampaigns()).thenReturn(List.of());
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void tooEarly(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropCampaign.getStartAt()).thenReturn(NOW.plusSeconds(1));
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void tooLate(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropCampaign.getEndAt()).thenReturn(NOW.minusSeconds(1));
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void requiresSubscription(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropCampaignSummary.isIncludesSubRequirement()).thenReturn(true);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void noDrops(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropCampaign.getTimeBasedDrops()).thenReturn(List.of());
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void dropTooEarly(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(timeBasedDrop.getStartAt()).thenReturn(NOW.plusSeconds(1));
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void dropTooLate(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(timeBasedDrop.getEndAt()).thenReturn(NOW.minusSeconds(1));
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void noBenefit(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(timeBasedDrop.getBenefitEdges()).thenReturn(List.of());
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(0);
		}
	}
	
	@Test
	void claimLimitReached(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(userDropReward.getTotalCount()).thenReturn(DROP_CLAIM_LIMIT);
			
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
			
			when(dropCampaign.getStartAt()).thenReturn(null);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
		}
	}
	
	@Test
	void validNoCampaignEndDate(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(dropCampaign.getEndAt()).thenReturn(null);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
		}
	}
	
	@Test
	void validNoDropStartDate(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(timeBasedDrop.getStartAt()).thenReturn(null);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
		}
	}
	
	@Test
	void validNoDropEndDate(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(timeBasedDrop.getEndAt()).thenReturn(null);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
		}
	}
	
	@Test
	void noInventoryIsSameAsZeroInInventory(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::nowZoned).thenReturn(NOW);
			
			when(minerData.getInventory()).thenReturn(null);
			
			assertThat(tested.getScore(miner, streamer)).isEqualTo(SCORE);
		}
	}
}