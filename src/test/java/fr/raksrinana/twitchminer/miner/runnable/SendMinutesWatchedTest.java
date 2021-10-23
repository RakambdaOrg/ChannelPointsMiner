package fr.raksrinana.twitchminer.miner.runnable;

import fr.raksrinana.twitchminer.api.gql.data.types.Game;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import fr.raksrinana.twitchminer.api.twitch.TwitchApi;
import fr.raksrinana.twitchminer.api.twitch.data.MinuteWatchedEvent;
import fr.raksrinana.twitchminer.api.twitch.data.MinuteWatchedProperties;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.streamer.Streamer;
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
	private static final int USER_ID = 123456789;
	private static final String GAME_NAME = "game-name";
	
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
	
	private URL spadeUrl;
	
	@BeforeEach
	void setUp() throws MalformedURLException{
		spadeUrl = new URL("https://google.com/");
		
		lenient().when(miner.getTwitchApi()).thenReturn(twitchApi);
		lenient().when(miner.getStreamers()).thenReturn(List.of(streamer));
		lenient().when(miner.getTwitchLogin()).thenReturn(twitchLogin);
		
		lenient().when(twitchLogin.getUserIdAsInt()).thenReturn(USER_ID);
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
		lenient().when(streamer.getStreamId()).thenReturn(Optional.of(STREAM_ID));
		lenient().when(streamer.isStreaming()).thenReturn(true);
	}
	
	@Test
	void sendingMinutesWatched(){
		when(streamer.getGame()).thenReturn(Optional.of(game));
		when(game.getName()).thenReturn(GAME_NAME);
		
		var expected = MinuteWatchedEvent.builder()
				.properties(MinuteWatchedProperties.builder()
						.channelId(STREAMER_ID)
						.broadcastId(STREAM_ID)
						.player(SITE_PLAYER)
						.userId(USER_ID)
						.game(GAME_NAME)
						.build())
				.build();
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi).sendPlayerEvents(spadeUrl, expected);
	}
	
	@Test
	void sendingMinutesWatchedNoGameName(){
		when(streamer.getGame()).thenReturn(Optional.of(game));
		
		var expected = MinuteWatchedEvent.builder()
				.properties(MinuteWatchedProperties.builder()
						.channelId(STREAMER_ID)
						.broadcastId(STREAM_ID)
						.player(SITE_PLAYER)
						.userId(USER_ID)
						.build())
				.build();
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi).sendPlayerEvents(spadeUrl, expected);
	}
	
	@Test
	void sendingMinutesWatchedNoGame(){
		var expected = MinuteWatchedEvent.builder()
				.properties(MinuteWatchedProperties.builder()
						.channelId(STREAMER_ID)
						.broadcastId(STREAM_ID)
						.player(SITE_PLAYER)
						.userId(USER_ID)
						.build())
				.build();
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi).sendPlayerEvents(spadeUrl, expected);
	}
	
	@Test
	void sendingMinutesWatchedNotStreaming(){
		when(streamer.isStreaming()).thenReturn(false);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi, never()).sendPlayerEvents(any(), any());
	}
	
	@Test
	void sendingMinutesWatchedNoSpadeUrl(){
		when(streamer.getSpadeUrl()).thenReturn(null);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi, never()).sendPlayerEvents(any(), any());
	}
	
	@Test
	void sendingMinutesWatchedSeveralStreamers(){
		when(miner.getStreamers()).thenReturn(List.of(streamer, streamer));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi, times(2)).sendPlayerEvents(any(), any());
	}
	
	@Test
	void sendingMinutesWatchedMaxTwoStreamers(){
		when(miner.getStreamers()).thenReturn(List.of(streamer, streamer, streamer, streamer));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(twitchApi, times(2)).sendPlayerEvents(any(), any());
	}
	
	@Test
	void sendingMinutesWatchedException(){
		when(twitchApi.sendPlayerEvents(any(), any())).thenThrow(new RuntimeException("For tests"));
		
		assertDoesNotThrow(() -> tested.run());
	}
}