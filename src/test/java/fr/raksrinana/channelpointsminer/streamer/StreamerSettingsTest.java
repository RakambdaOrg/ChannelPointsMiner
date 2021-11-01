package fr.raksrinana.channelpointsminer.streamer;

import fr.raksrinana.channelpointsminer.priority.StreamerPriority;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StreamerSettingsTest{
	@Mock
	private StreamerPriority priority;
	
	@Test
	void copy(){
		var tested = StreamerSettings.builder()
				.makePredictions(true)
				.participateCampaigns(true)
				.followRaid(true)
				.joinIrc(true)
				.priorities(List.of(priority))
				.build();
		
		var copy = new StreamerSettings(tested);
		
		assertThat(copy).isNotSameAs(tested);
		assertThat(copy.isMakePredictions()).isEqualTo(tested.isMakePredictions());
		assertThat(copy.isParticipateCampaigns()).isEqualTo(tested.isParticipateCampaigns());
		assertThat(copy.isFollowRaid()).isEqualTo(tested.isFollowRaid());
		assertThat(copy.isJoinIrc()).isEqualTo(tested.isJoinIrc());
		
		assertThat(copy.getPriorities()).isNotSameAs(tested.getPriorities()).hasSize(1);
		assertThat(copy.getPriorities().get(0)).isSameAs(priority);
		
		assertThat(copy.getPredictions()).isNotSameAs(tested.getPredictions()).isEqualTo(tested.getPredictions());
	}
}