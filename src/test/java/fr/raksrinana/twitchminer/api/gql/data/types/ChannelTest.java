package fr.raksrinana.twitchminer.api.gql.data.types;

import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelTest{
	@Mock
	private ChannelSelfEdge self;
	@Mock
	private CommunityPointsClaim communityPointsClaim;
	
	@Test
	void getClaim(){
		when(self.getClaim()).thenReturn(Optional.of(communityPointsClaim));
		
		var tested = Channel.builder().self(self).build();
		Assertions.assertThat(tested.getClaim()).isPresent().get().isEqualTo(communityPointsClaim);
	}
	
	@Test
	void getClaimEmpty(){
		when(self.getClaim()).thenReturn(Optional.empty());
		
		var tested = Channel.builder().self(self).build();
		Assertions.assertThat(tested.getClaim()).isEmpty();
	}
	
	@Test
	void getClaimNull(){
		var tested = Channel.builder().build();
		Assertions.assertThat(tested.getClaim()).isEmpty();
	}
}