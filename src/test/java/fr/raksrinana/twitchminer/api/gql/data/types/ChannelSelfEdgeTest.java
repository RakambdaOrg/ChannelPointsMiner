package fr.raksrinana.twitchminer.api.gql.data.types;

import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelSelfEdgeTest{
	@Mock
	private CommunityPointsProperties communityPointsProperties;
	@Mock
	private CommunityPointsClaim communityPointsClaim;
	
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
}