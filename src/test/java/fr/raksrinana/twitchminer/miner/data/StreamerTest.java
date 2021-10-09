package fr.raksrinana.twitchminer.miner.data;

import fr.raksrinana.twitchminer.api.gql.data.types.Game;
import fr.raksrinana.twitchminer.api.gql.data.types.Stream;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreamerTest{
	private static final String USERNAME = "username";
	
	private Streamer tested;
	
	@Mock
	private StreamerSettings settings;
	@Mock
	private VideoPlayerStreamInfoOverlayChannelData videoPlayerStreamInfoOverlayChannelData;
	@Mock
	private Game game;
	@Mock
	private User user;
	
	@BeforeEach
	void setUp(){
		tested = new Streamer("streamer-id", USERNAME, settings);
		tested.setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
	}
	
	@Test
	void getGame(){
		when(videoPlayerStreamInfoOverlayChannelData.getGame()).thenReturn(Optional.of(game));
		
		assertThat(tested.getGame()).isPresent().get().isEqualTo(game);
	}
	
	@Test
	void getGameNoData(){
		tested.setVideoPlayerStreamInfoOverlayChannel(null);
		
		assertThat(tested.getGame()).isEmpty();
	}
	
	@Test
	void getStreamId(){
		var streamId = "stream-id";
		when(videoPlayerStreamInfoOverlayChannelData.getStream()).thenReturn(Optional.of(Stream.builder().id(streamId).build()));
		
		assertThat(tested.getStreamId()).isPresent().get().isEqualTo(streamId);
	}
	
	@Test
	void getStreamIdNoData(){
		tested.setVideoPlayerStreamInfoOverlayChannel(null);
		
		assertThat(tested.getStreamId()).isEmpty();
	}
	
	@Test
	void getChannelUrl() throws MalformedURLException{
		assertThat(tested.getChannelUrl()).isEqualTo(new URL("https://www.twitch.tv/" + USERNAME));
	}
	
	@Test
	void streaming(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		when(user.isStreaming()).thenReturn(true);
		
		assertThat(tested.isStreaming()).isTrue();
	}
	
	@Test
	void streamingNoData(){
		tested.setVideoPlayerStreamInfoOverlayChannel(null);
		
		assertThat(tested.isStreaming()).isFalse();
	}
	
	@Test
	void notStreaming(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		when(user.isStreaming()).thenReturn(false);
		
		assertThat(tested.isStreaming()).isFalse();
	}
	
	@Test
	void streamingGame(){
		when(videoPlayerStreamInfoOverlayChannelData.getGame()).thenReturn(Optional.of(game));
		when(game.getName()).thenReturn("GAME");
		
		assertThat(tested.isStreamingGame()).isTrue();
	}
	
	@Test
	void streamingGameNoData(){
		tested.setVideoPlayerStreamInfoOverlayChannel(null);
		
		assertThat(tested.isStreamingGame()).isFalse();
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"    "})
	void notStreamingGame(String name){
		when(videoPlayerStreamInfoOverlayChannelData.getGame()).thenReturn(Optional.of(game));
		when(game.getName()).thenReturn(name);
		
		assertThat(tested.isStreamingGame()).isFalse();
	}
}