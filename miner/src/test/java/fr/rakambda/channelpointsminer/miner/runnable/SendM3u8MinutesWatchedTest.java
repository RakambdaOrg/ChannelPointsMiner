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
import java.net.URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.lenient;
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
		m3u8Url = new URL("https://google.com/");
		
		lenient().when(miner.getTwitchApi()).thenReturn(twitchApi);
		lenient().when(streamer.getM3u8Url()).thenReturn(m3u8Url);
	}
	
	@Test
	void sendingMinutesWatched(){
		when(twitchApi.openM3u8LastChunk(m3u8Url)).thenReturn(true);
		
		assertDoesNotThrow(() -> tested.send(streamer));
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