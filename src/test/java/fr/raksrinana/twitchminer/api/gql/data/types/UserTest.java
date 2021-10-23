package fr.raksrinana.twitchminer.api.gql.data.types;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTest{
	@Mock
	private Stream stream;
	@Mock
	private BroadcastSettings broadcastSettings;
	@Mock
	private Game game;
	@Mock
	private Channel channel;
	@Mock
	private CommunityPointsClaim communityPointsClaim;
	@Mock
	private List<CommunityPointsMultiplier> multipliers;
	
	@Test
	void isStream(){
		var tested = User.builder().id("id").stream(stream).build();
		
		assertThat(tested.isStreaming()).isTrue();
	}
	
	@Test
	void isNotStream(){
		var tested = User.builder().id("id").build();
		
		assertThat(tested.isStreaming()).isFalse();
	}
	
	@Test
	void getGame(){
		var tested = User.builder().id("id").broadcastSettings(broadcastSettings).build();
		
		when(broadcastSettings.getGame()).thenReturn(game);
		
		assertThat(tested.getGame()).isPresent().get().isSameAs(game);
	}
	
	@Test
	void getGameEmptyNoGame(){
		var tested = User.builder().id("id").broadcastSettings(broadcastSettings).build();
		
		when(broadcastSettings.getGame()).thenReturn(null);
		
		assertThat(tested.getGame()).isEmpty();
	}
	
	@Test
	void getGameEmptyNoBroadcast(){
		var tested = User.builder().id("id").build();
		
		assertThat(tested.getGame()).isEmpty();
	}
	
	@Test
	void getClaim(){
		var tested = User.builder().id("id").channel(channel).build();
		
		when(channel.getClaim()).thenReturn(Optional.of(communityPointsClaim));
		
		assertThat(tested.getClaim()).isPresent().get().isEqualTo(communityPointsClaim);
	}
	
	@Test
	void getClaimEmpty(){
		var tested = User.builder().id("id").channel(channel).build();
		
		when(channel.getClaim()).thenReturn(Optional.empty());
		
		assertThat(tested.getClaim()).isEmpty();
	}
	
	@Test
	void getClaimNull(){
		var tested = User.builder().id("id").build();
		
		assertThat(tested.getClaim()).isEmpty();
	}
	
	@Test
	void getMultipliers(){
		var tested = User.builder().id("id").channel(channel).build();
		
		when(channel.getMultipliers()).thenReturn(Optional.of(multipliers));
		
		assertThat(tested.getMultipliers()).isPresent().get().isEqualTo(multipliers);
	}
	
	@Test
	void getMultipliersEmpty(){
		var tested = User.builder().id("id").channel(channel).build();
		
		when(channel.getMultipliers()).thenReturn(Optional.empty());
		
		assertThat(tested.getMultipliers()).isEmpty();
	}
	
	@Test
	void getMultipliersNull(){
		var tested = User.builder().id("id").build();
		
		assertThat(tested.getMultipliers()).isEmpty();
	}
}