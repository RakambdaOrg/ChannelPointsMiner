package fr.rakambda.channelpointsminer.miner.streamer;

import fr.rakambda.channelpointsminer.miner.priority.IStreamerPriority;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class StreamerSettingsTest{
	@Mock
	private IStreamerPriority priority;
	
	@Test
	void copy(){
		var tested = StreamerSettings.builder()
				.makePredictions(true)
				.participateCampaigns(true)
				.dismissKnownGlobalCampaigns(true)
				.followRaid(true)
				.excludeSubscriberDrops(false)
				.joinIrc(true)
				.index(24)
				.priorities(List.of(priority))
				.claimMoments(true)
				.build();
		
		var copy = new StreamerSettings(tested);
		
		assertThat(copy).isNotSameAs(tested);
		assertThat(copy.isEnabled()).isEqualTo(tested.isEnabled());
		assertThat(copy.isMakePredictions()).isEqualTo(tested.isMakePredictions());
		assertThat(copy.isParticipateCampaigns()).isEqualTo(tested.isParticipateCampaigns());
		assertThat(copy.isDismissKnownGlobalCampaigns()).isEqualTo(tested.isDismissKnownGlobalCampaigns());
		assertThat(copy.isExcludeSubscriberDrops()).isEqualTo(tested.isExcludeSubscriberDrops());
		assertThat(copy.isFollowRaid()).isEqualTo(tested.isFollowRaid());
		assertThat(copy.isJoinIrc()).isEqualTo(tested.isJoinIrc());
		assertThat(copy.getIndex()).isEqualTo(tested.getIndex());
		assertThat(copy.isClaimMoments()).isEqualTo(tested.isClaimMoments());
		
		assertThat(copy.getPriorities()).isNotSameAs(tested.getPriorities()).hasSize(1);
		Assertions.assertThat(copy.getPriorities().get(0)).isSameAs(priority);
		
		assertThat(copy.getPredictions()).isNotSameAs(tested.getPredictions()).isEqualTo(tested.getPredictions());
	}
}