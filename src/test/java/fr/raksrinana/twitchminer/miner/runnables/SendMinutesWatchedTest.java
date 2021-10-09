package fr.raksrinana.twitchminer.miner.runnables;

import fr.raksrinana.twitchminer.api.gql.data.types.Game;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import fr.raksrinana.twitchminer.api.twitch.MinuteWatchedProperties;
import fr.raksrinana.twitchminer.api.twitch.MinuteWatchedRequest;
import fr.raksrinana.twitchminer.api.twitch.TwitchApi;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendMinutesWatchedTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAM_ID = "stream-id";
	private static final String SITE_PLAYER = "site";
	private static final String USER_ID = "user-id";
	private static final String GAME_NAME = "game-name";
	private static URL SPADE_URL;
	
	@InjectMocks
	private SendMinutesWatched tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private TwitchApi twitchApi;
	@Mock
	private Streamer streamer;
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private Game game;
	
	@BeforeEach
	void setUp() throws MalformedURLException{
		SPADE_URL = new URL("https://google.com/");
		
		lenient().when(miner.getTwitchApi()).thenReturn(twitchApi);
		lenient().when(miner.getStreamers()).thenReturn(List.of(streamer));
		lenient().when(miner.getTwitchLogin()).thenReturn(twitchLogin);
		
		lenient().when(twitchLogin.getUserId()).thenReturn(USER_ID);
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getSpadeUrl()).thenReturn(SPADE_URL);
		lenient().when(streamer.getStreamId()).thenReturn(Optional.of(STREAM_ID));
		lenient().when(streamer.isStreaming()).thenReturn(true);
	}
	
	@Test
	void sendingMinutesWatched(){
		when(streamer.getGame()).thenReturn(Optional.of(game));
		when(game.getName()).thenReturn(GAME_NAME);
		
		var expected = new MinuteWatchedRequest(MinuteWatchedProperties.builder()
				.channelId(STREAMER_ID)
				.broadcastId(STREAM_ID)
				.player(SITE_PLAYER)
				.userId(USER_ID)
				.game(GAME_NAME)
				.build());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi).sendMinutesWatched(SPADE_URL, expected);
	}
	
	@Test
	void sendingMinutesWatchedNoGameName(){
		when(streamer.getGame()).thenReturn(Optional.of(game));
		
		var expected = new MinuteWatchedRequest(MinuteWatchedProperties.builder()
				.channelId(STREAMER_ID)
				.broadcastId(STREAM_ID)
				.player(SITE_PLAYER)
				.userId(USER_ID)
				.build());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi).sendMinutesWatched(SPADE_URL, expected);
	}
	
	@Test
	void sendingMinutesWatchedNoGame(){
		var expected = new MinuteWatchedRequest(MinuteWatchedProperties.builder()
				.channelId(STREAMER_ID)
				.broadcastId(STREAM_ID)
				.player(SITE_PLAYER)
				.userId(USER_ID)
				.build());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi).sendMinutesWatched(SPADE_URL, expected);
	}
	
	@Test
	void sendingMinutesWatchedNotStreaming(){
		when(streamer.isStreaming()).thenReturn(false);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi, never()).sendMinutesWatched(any(), any());
	}
	
	@Test
	void sendingMinutesWatchedNoSpadeUrl(){
		when(streamer.getSpadeUrl()).thenReturn(null);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi, never()).sendMinutesWatched(any(), any());
	}
	
	@Test
	void sendingMinutesWatchedSeveralStreamers(){
		when(miner.getStreamers()).thenReturn(List.of(streamer, streamer));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi, times(2)).sendMinutesWatched(any(), any());
	}
	
	@Test
	void sendingMinutesWatchedMaxTwoStreamers(){
		when(miner.getStreamers()).thenReturn(List.of(streamer, streamer, streamer, streamer));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi, times(2)).sendMinutesWatched(any(), any());
	}
}