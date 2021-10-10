package fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel;

import fr.raksrinana.twitchminer.api.gql.data.types.Game;
import fr.raksrinana.twitchminer.api.gql.data.types.Stream;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoPlayerStreamInfoOverlayChannelDataTest{
	@Mock
	private User user;
	@Mock
	private Game game;
	@Mock
	private Stream stream;
	
	@Test
	void getGame(){
		var tested = VideoPlayerStreamInfoOverlayChannelData.builder().user(user).build();
		
		when(user.getGame()).thenReturn(Optional.of(game));
		
		assertThat(tested.getGame()).isPresent().get().isSameAs(game);
	}
	
	@Test
	void getGameEmpty(){
		var tested = VideoPlayerStreamInfoOverlayChannelData.builder().user(user).build();
		
		when(user.getGame()).thenReturn(Optional.empty());
		
		assertThat(tested.getGame()).isEmpty();
	}
	
	@Test
	void getStream(){
		var tested = VideoPlayerStreamInfoOverlayChannelData.builder().user(user).build();
		
		when(user.getStream()).thenReturn(stream);
		
		assertThat(tested.getStream()).isPresent().get().isSameAs(stream);
	}
	
	
	@Test
	void getStreamEmpty(){
		var tested = VideoPlayerStreamInfoOverlayChannelData.builder().user(user).build();
		
		when(user.getStream()).thenReturn(null);
		
		assertThat(tested.getStream()).isEmpty();
	}
}