package fr.raksrinana.twitchminer.api.gql.data.channelpointscontext;

import fr.raksrinana.twitchminer.api.gql.data.types.CommunityPointsClaim;
import fr.raksrinana.twitchminer.api.gql.data.types.CommunityPointsMultiplier;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelPointsContextDataTest{
	@Mock
	private User community;
	@Mock
	private User user;
	@Mock
	private CommunityPointsClaim communityPointsClaim;
	@Mock
	private List<CommunityPointsMultiplier> multipliers;
	
	@Test
	void getClaim(){
		when(community.getClaim()).thenReturn(Optional.of(communityPointsClaim));
		
		var tested = ChannelPointsContextData.builder().currentUser(user).community(community).build();
		assertThat(tested.getClaim()).isPresent().get().isEqualTo(communityPointsClaim);
	}
	
	@Test
	void getMultipliers(){
		when(community.getMultipliers()).thenReturn(Optional.of(multipliers));
		
		var tested = ChannelPointsContextData.builder().currentUser(user).community(community).build();
		assertThat(tested.getMultipliers()).isPresent().get().isEqualTo(multipliers);
	}
}