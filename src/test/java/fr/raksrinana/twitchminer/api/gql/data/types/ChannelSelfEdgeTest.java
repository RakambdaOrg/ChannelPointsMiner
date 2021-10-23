package fr.raksrinana.twitchminer.api.gql.data.types;

import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelSelfEdgeTest{
	@Mock
	private CommunityPointsProperties communityPointsProperties;
	@Mock
	private CommunityPointsClaim communityPointsClaim;
	@Mock
	private List<CommunityPointsMultiplier> multipliers;
	
	@Test
	void getClaim(){
		when(communityPointsProperties.getAvailableClaim()).thenReturn(communityPointsClaim);
		
		var tested = ChannelSelfEdge.builder().communityPoints(communityPointsProperties).build();
		Assertions.assertThat(tested.getClaim()).isPresent().get().isEqualTo(communityPointsClaim);
	}
	
	@Test
	void getClaimEmpty(){
		var tested = ChannelSelfEdge.builder().communityPoints(communityPointsProperties).build();
		Assertions.assertThat(tested.getClaim()).isEmpty();
	}
	
	@Test
	void getMultipliers(){
		when(communityPointsProperties.getActiveMultipliers()).thenReturn(multipliers);
		
		var tested = ChannelSelfEdge.builder().communityPoints(communityPointsProperties).build();
		Assertions.assertThat(tested.getMultipliers()).isEqualTo(multipliers);
	}
}