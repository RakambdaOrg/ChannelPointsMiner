package fr.raksrinana.twitchminer.api.gql.data.types;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
}