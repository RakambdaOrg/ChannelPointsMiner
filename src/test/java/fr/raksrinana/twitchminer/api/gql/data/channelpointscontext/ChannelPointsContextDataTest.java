package fr.raksrinana.twitchminer.api.gql.data.channelpointscontext;

import fr.raksrinana.twitchminer.api.gql.data.types.CommunityPointsClaim;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelPointsContextDataTest{
	@Mock
	private User user;
	@Mock
	private CommunityPointsClaim communityPointsClaim;
	
	@Test
	void getClaim(){
		when(user.getClaim()).thenReturn(Optional.of(communityPointsClaim));
		
		var tested = ChannelPointsContextData.builder().community(user).build();
		assertThat(tested.getClaim()).isPresent().get().isEqualTo(communityPointsClaim);
	}
	
	@Test
	void getClaimEmpty(){
		when(user.getClaim()).thenReturn(Optional.empty());
		
		var tested = ChannelPointsContextData.builder().community(user).build();
		assertThat(tested.getClaim()).isEmpty();
	}
}