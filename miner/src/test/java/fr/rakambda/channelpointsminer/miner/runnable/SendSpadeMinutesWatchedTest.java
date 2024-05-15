package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Game;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.rakambda.channelpointsminer.miner.api.twitch.data.MinuteWatchedEvent;
import fr.rakambda.channelpointsminer.miner.api.twitch.data.MinuteWatchedProperties;
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
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class SendSpadeMinutesWatchedTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_NAME = "streamer-name";
	private static final String STREAM_ID = "stream-id";
	private static final String SITE_PLAYER = "site";
	private static final int USER_ID = 123456789;
	private static final String GAME_NAME = "game-name";
	private static final String GAME_ID = "game-id";
	
	@InjectMocks
	private SendSpadeMinutesWatched tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private TwitchApi twitchApi;
	@Mock
	private GQLApi gqlApi;
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
		lenient().when(miner.getTwitchLogin()).thenReturn(twitchLogin);
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		
		lenient().when(twitchLogin.getUserIdAsInt(gqlApi)).thenReturn(USER_ID);
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_NAME);
		lenient().when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
		lenient().when(streamer.getStreamId()).thenReturn(Optional.of(STREAM_ID));
		
		lenient().when(game.getName()).thenReturn(GAME_NAME);
		lenient().when(game.getId()).thenReturn(GAME_ID);
	}
	
	@Test
	void sendingMinutesWatched(){
		when(streamer.getGame()).thenReturn(Optional.of(game));
		
		var expected = MinuteWatchedEvent.builder()
				.properties(MinuteWatchedProperties.builder()
						.channelId(STREAMER_ID)
						.channel(STREAMER_NAME)
						.broadcastId(STREAM_ID)
						.player(SITE_PLAYER)
						.userId(USER_ID)
						.gameId(GAME_ID)
						.game(GAME_NAME)
						.live(true)
						.build())
				.build();
		
		when(twitchApi.sendPlayerEvents(spadeUrl, expected)).thenReturn(true);
		
		assertThat(tested.send(streamer)).isTrue();
	}
	
	@Test
	void sendingMinutesWatchedNoGameName(){
		when(streamer.getGame()).thenReturn(Optional.of(game));
		when(game.getName()).thenReturn(null);
		when(game.getId()).thenReturn(null);
		
		var expected = MinuteWatchedEvent.builder()
				.properties(MinuteWatchedProperties.builder()
						.channelId(STREAMER_ID)
						.channel(STREAMER_NAME)
						.broadcastId(STREAM_ID)
						.player(SITE_PLAYER)
						.userId(USER_ID)
						.live(true)
						.build())
				.build();
		
		when(twitchApi.sendPlayerEvents(spadeUrl, expected)).thenReturn(true);
		
		assertThat(tested.send(streamer)).isTrue();
	}
	
	@Test
	void sendingMinutesWatchedNoGame(){
		var expected = MinuteWatchedEvent.builder()
				.properties(MinuteWatchedProperties.builder()
						.channelId(STREAMER_ID)
						.channel(STREAMER_NAME)
						.broadcastId(STREAM_ID)
						.player(SITE_PLAYER)
						.userId(USER_ID)
						.live(true)
						.build())
				.build();
		
		when(twitchApi.sendPlayerEvents(spadeUrl, expected)).thenReturn(true);
		
		assertThat(tested.send(streamer)).isTrue();
	}
	
	@Test
	void sendingMinutesWatchedNoStreamId(){
		when(streamer.getStreamId()).thenReturn(Optional.empty());
		
		assertThat(tested.send(streamer)).isFalse();
		
		verify(twitchApi, never()).sendPlayerEvents(any(), any());
	}
	
	@Test
	void checkValid(){
		assertThat(tested.checkStreamer(streamer)).isTrue();
	}
	
	@Test
	void checkInvalid(){
		when(streamer.getSpadeUrl()).thenReturn(null);
		
		assertThat(tested.checkStreamer(streamer)).isFalse();
	}
}