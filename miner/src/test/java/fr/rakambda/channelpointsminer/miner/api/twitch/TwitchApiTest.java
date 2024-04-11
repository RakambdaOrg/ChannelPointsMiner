package fr.rakambda.channelpointsminer.miner.api.twitch;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.rakambda.channelpointsminer.miner.api.twitch.data.MinuteWatchedEvent;
import fr.rakambda.channelpointsminer.miner.api.twitch.data.MinuteWatchedProperties;
import fr.rakambda.channelpointsminer.miner.api.twitch.data.PlayerEvent;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import static java.nio.charset.StandardCharsets.UTF_8;
import static kong.unirest.core.HttpMethod.GET;
import static kong.unirest.core.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
@ParallelizableTest
class TwitchApiTest{
    private static final int USER_ID = 123456789;
    private static final String BROADCAST_ID = "broadcast-id";
    private static final String CHANNEL_ID = "channel-id";
	private static final String CHANNEL_NAME = "channel-name";
    private static final String PLAYER = "player";
    private static final String GAME = "game";
	private static final String GAME_ID = "game-id";
    private static final String STREAMER_URL = "https://google.com/streamer";
    private static final String CONFIG_URL = "https://assets.twitch.tv/config/settings.sq5d4q6s54ds854c84qs.js";
    private static final String CONFIG_BODY = "<script src=\"%s\" crossorigin=\"anonymous\"></script>".formatted(CONFIG_URL);
    private static final String SPADE_URL = "https://google.com";
    private static final String SPADE_BODY = "azeazeazeaze\"spade_url\":\"%s\"azeazeaze".formatted(SPADE_URL);
    private static final String SPADE_STREAMER_BODY = "azeazeazeaze\"spadeUrl\":\"%s\"azeazeaze".formatted(SPADE_URL);
    private static final String SPADE_BODY_INVALID_FORMAT = "\"spade_url\":\"%s\"".formatted("https://google.com:-80/");
    
    private TwitchApi tested;
    
    private URL streamerUrl;
    private URL spadeUrl;
    
    @BeforeEach
    void setUp(UnirestMock unirestMock) throws MalformedURLException{
        streamerUrl = new URL(STREAMER_URL);
        spadeUrl = new URL(SPADE_URL);
        
        tested = new TwitchApi(unirestMock.getUnirestInstance());
    }
    
    @Test
    void sendMinutesWatched(UnirestMock unirest){
	    var json = "[{\"event\":\"minute-watched\",\"properties\":{\"broadcast_id\":\"%s\",\"channel\":\"%s\",\"channel_id\":\"%s\",\"live\":true,\"player\":\"%s\",\"user_id\":%d}}]"
			    .formatted(BROADCAST_ID, CHANNEL_NAME, CHANNEL_ID, PLAYER, USER_ID);
        var expectedData = new String(Base64.getEncoder().encode(json.getBytes(UTF_8)));
        
        unirest.expect(POST, SPADE_URL)
                .body("data=%s".formatted(expectedData))
                .thenReturn()
                .withStatus(204);
        
        var request = MinuteWatchedEvent.builder()
                .properties(MinuteWatchedProperties.builder()
                        .userId(USER_ID)
                        .broadcastId(BROADCAST_ID)
                        .channelId(CHANNEL_ID)
		                .channel(CHANNEL_NAME)
                        .player(PLAYER)
		                .live(true)
                        .build())
                .build();
        assertThat(tested.sendPlayerEvents(spadeUrl, request)).isTrue();
        
        unirest.verifyAll();
    }
    
    @Test
    void sendMinutesWatchedWithGame(UnirestMock unirest){
	    var json = "[{\"event\":\"minute-watched\",\"properties\":{\"broadcast_id\":\"%s\",\"channel\":\"%s\",\"channel_id\":\"%s\",\"game\":\"%s\",\"game_id\":\"%s\",\"live\":true,\"player\":\"%s\",\"user_id\":%d}}]"
			    .formatted(BROADCAST_ID, CHANNEL_NAME, CHANNEL_ID, GAME, GAME_ID, PLAYER, USER_ID);
        var expectedData = new String(Base64.getEncoder().encode(json.getBytes(UTF_8)));
        
        unirest.expect(POST, SPADE_URL)
                .body("data=%s".formatted(expectedData))
                .thenReturn()
                .withStatus(204);
        
        var request = MinuteWatchedEvent.builder()
                .properties(MinuteWatchedProperties.builder()
                        .userId(USER_ID)
                        .broadcastId(BROADCAST_ID)
		                .channelId(CHANNEL_ID)
		                .channel(CHANNEL_NAME)
		                .player(PLAYER)
                        .game(GAME)
		                .gameId(GAME_ID)
		                .live(true)
                        .build())
                .build();
        assertThat(tested.sendPlayerEvents(spadeUrl, request)).isTrue();
        
        unirest.verifyAll();
    }
    
    @Test
    void sendMinutesWatchedNotSuccess(UnirestMock unirest){
	    var json = "[{\"event\":\"minute-watched\",\"properties\":{\"broadcast_id\":\"%s\",\"channel\":\"%s\",\"channel_id\":\"%s\",\"live\":true,\"player\":\"%s\",\"user_id\":%d}}]"
			    .formatted(BROADCAST_ID, CHANNEL_NAME, CHANNEL_ID, PLAYER, USER_ID);
        var expectedData = new String(Base64.getEncoder().encode(json.getBytes(UTF_8)));
        
        unirest.expect(POST, SPADE_URL)
                .body("data=%s".formatted(expectedData))
                .thenReturn()
                .withStatus(400);
        
        var request = MinuteWatchedEvent.builder()
                .properties(MinuteWatchedProperties.builder()
                        .userId(USER_ID)
                        .broadcastId(BROADCAST_ID)
                        .channelId(CHANNEL_ID)
		                .channel(CHANNEL_NAME)
                        .player(PLAYER)
		                .live(true)
                        .build())
                .build();
        assertThat(tested.sendPlayerEvents(spadeUrl, request)).isFalse();
        
        unirest.verifyAll();
    }
    
    @Test
    void sendMinutesWatchedJsonError(){
        try(var jacksonUtils = Mockito.mockStatic(JacksonUtils.class)){
            var exception = mock(JsonProcessingException.class);
            when(exception.getStackTrace()).thenReturn(new StackTraceElement[0]);
            jacksonUtils.when(() -> JacksonUtils.writeAsString(any(PlayerEvent[].class))).thenThrow(exception);
            
            var request = MinuteWatchedEvent.builder()
                    .properties(MinuteWatchedProperties.builder()
                            .userId(USER_ID)
                            .broadcastId(BROADCAST_ID)
                            .channelId(CHANNEL_ID)
		                    .channel(CHANNEL_NAME)
                            .player(PLAYER)
		                    .live(true)
                            .build())
                    .build();
            assertThat(tested.sendPlayerEvents(spadeUrl, request)).isFalse();
        }
    }
    
    @Test
    void getSpadeUrl(UnirestMock unirest){
        unirest.expect(GET, STREAMER_URL)
                .thenReturn(CONFIG_BODY)
                .withStatus(200);
        
        unirest.expect(GET, CONFIG_URL)
                .thenReturn(SPADE_BODY)
                .withStatus(200);
        
        assertThat(tested.getSpadeUrl(streamerUrl)).isPresent()
                .get().isEqualTo(spadeUrl);
    }
    
    @Test
    void getSpadeUrlFromStreamerPage(UnirestMock unirest){
        unirest.expect(GET, STREAMER_URL)
                .thenReturn(SPADE_STREAMER_BODY)
                .withStatus(200);
        
        assertThat(tested.getSpadeUrl(streamerUrl)).isPresent()
                .get().isEqualTo(spadeUrl);
    }
    
    @Test
    void getSpadeUrlInvalidConfigUrlResponse(UnirestMock unirest){
        unirest.expect(GET, STREAMER_URL)
                .thenReturn(CONFIG_BODY)
                .withStatus(500);
        
        assertThat(tested.getSpadeUrl(streamerUrl)).isEmpty();
    }
    
    @Test
    void getSpadeUrlNoConfigUrl(UnirestMock unirest){
        unirest.expect(GET, STREAMER_URL)
                .thenReturn("")
                .withStatus(200);
        
        assertThat(tested.getSpadeUrl(streamerUrl)).isEmpty();
    }
    
    @Test
    void getSpadeUrlInvalidResponse(UnirestMock unirest){
        unirest.expect(GET, STREAMER_URL)
                .thenReturn(CONFIG_BODY)
                .withStatus(200);
        
        unirest.expect(GET, CONFIG_URL)
                .thenReturn(SPADE_BODY)
                .withStatus(500);
        
        assertThat(tested.getSpadeUrl(streamerUrl)).isEmpty();
    }
    
    @Test
    void getSpadeUrlInvalidFormat(UnirestMock unirest){
        unirest.expect(GET, STREAMER_URL)
                .thenReturn(CONFIG_BODY)
                .withStatus(200);
        
        unirest.expect(GET, CONFIG_URL)
                .thenReturn(SPADE_BODY_INVALID_FORMAT)
                .withStatus(200);
        
        assertThat(tested.getSpadeUrl(streamerUrl)).isEmpty();
    }
    
    @Test
    void getSpadeUrlNoUrl(UnirestMock unirest){
        unirest.expect(GET, STREAMER_URL)
                .thenReturn(CONFIG_BODY)
                .withStatus(200);
        
        unirest.expect(GET, CONFIG_URL)
                .thenReturn("")
                .withStatus(200);
        
        assertThat(tested.getSpadeUrl(streamerUrl)).isEmpty();
    }
}