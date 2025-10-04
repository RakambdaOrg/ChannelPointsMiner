package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class SendM3u8MinutesWatchedTest {
	@InjectMocks
	private SendM3u8MinutesWatched tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private TwitchApi twitchApi;
	@Mock
	private Streamer streamer;
	
	private URL m3u8Url;
	
	@BeforeEach
	void setUp() throws MalformedURLException{
		m3u8Url = URI.create("https://google.com/").toURL();
		
		lenient().when(miner.getTwitchApi()).thenReturn(twitchApi);
		lenient().when(streamer.getM3u8Url()).thenReturn(m3u8Url);
	}
	
	@Test
	void hasType(){
		assertThat(tested.getType()).isNotNull();
	}
	
	@Test
	void sendingMinutesWatched(){
		when(twitchApi.openM3u8LastChunk(m3u8Url)).thenReturn(true);
		
		assertDoesNotThrow(() -> tested.send(streamer));
		
		verify(streamer, never()).setM3u8Url(any());
	}
	
	@Test
	void sendingMinutesWatchedFailed(){
		when(twitchApi.openM3u8LastChunk(m3u8Url)).thenReturn(false);
		
		assertDoesNotThrow(() -> tested.send(streamer));
		
		verify(streamer).setM3u8Url(null);
	}
	
	@Test
	void checkValid(){
		assertThat(tested.checkStreamer(streamer)).isTrue();
	}
	
	@Test
	void checkInvalid(){
		when(streamer.getM3u8Url()).thenReturn(null);
		
		assertThat(tested.checkStreamer(streamer)).isFalse();
	}
}