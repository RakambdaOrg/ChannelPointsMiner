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
import java.net.URI;
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
	private static final String M3U8_SIGNATURE = "sig";
	private static final String M3U8_VALUE = "val";
	private static final String M3U8_URL = "https://usher.ttvnw.net/api/channel/hls/%s.m3u8".formatted(CHANNEL_NAME);
	private static final String M3U8_BODY = """
			#EXTM3U
			#EXT-X-TWITCH-INFO:NODE="video-edge-a.b",MANIFEST-NODE-TYPE="weaver_cluster",MANIFEST-NODE="video-weaver.a",SUPPRESS="false",SERVER-TIME="1.1",TRANSCODESTACK="2023-Transcode-QS-V1",TRANSCODEMODE="cbr_v1",USER-IP="1.1.1.1",SERVING-ID="serv",CLUSTER="clust",ABS="true",VIDEO-SESSION-ID="12345",BROADCAST-ID="12345",STREAM-TIME="1.1",B="false",USER-COUNTRY="AA",MANIFEST-CLUSTER="clust",ORIGIN="orig",C="data",D="false"
			#EXT-X-MEDIA:TYPE=VIDEO,GROUP-ID="480p30",NAME="480p",AUTOSELECT=YES,DEFAULT=YES
			#EXT-X-STREAM-INF:BANDWIDTH=1427999,RESOLUTION=852x480,CODECS="avc1.4D401F,mp4a.40.2",VIDEO="480p30",FRAME-RATE=30.000
			https://stream1.m3u8
			#EXT-X-MEDIA:TYPE=VIDEO,GROUP-ID="360p30",NAME="360p",AUTOSELECT=YES,DEFAULT=YES
			#EXT-X-STREAM-INF:BANDWIDTH=630000,RESOLUTION=640x360,CODECS="avc1.4D401F,mp4a.40.2",VIDEO="360p30",FRAME-RATE=30.000
			https://stream2.m3u8
			#EXT-X-MEDIA:TYPE=VIDEO,GROUP-ID="160p30",NAME="160p",AUTOSELECT=YES,DEFAULT=YES
			#EXT-X-STREAM-INF:BANDWIDTH=230000,RESOLUTION=284x160,CODECS="avc1.4D401F,mp4a.40.2",VIDEO="160p30",FRAME-RATE=30.000
			https://stream3.m3u8
			""";
	private static final String M3U8_CHUNK_URL = "https://video-edge-stream.test/chunk3.ts";
	private static final String M3U8_PLAYLIST_BODY = """
			#EXTM3U
			#EXT-X-VERSION:3
			#EXT-X-TARGETDURATION:6
			#EXT-X-MEDIA-SEQUENCE:2
			#EXT-X-TWITCH-LIVE-SEQUENCE:3
			#EXT-X-TWITCH-ELAPSED-SECS:1.1
			#EXT-X-TWITCH-TOTAL-SECS:2.2
			#EXT-X-DATERANGE:ID="playlist-creation-123",CLASS="timestamp",START-DATE="2024-05-15T09:10:00.427Z",END-ON-NEXT=YES,X-SERVER-TIME="1234.00"
			#EXT-X-DATERANGE:ID="playlist-session-123",CLASS="twitch-session",START-DATE="2024-05-15T09:10:00.427Z",END-ON-NEXT=YES,X-TV-TWITCH-SESSIONID="1234"
			#EXT-X-DATERANGE:ID="source-123",CLASS="twitch-stream-source",START-DATE="2024-05-15T09:09:00.348Z",END-ON-NEXT=YES,X-TV-TWITCH-STREAM-SOURCE="live"
			#EXT-X-DATERANGE:ID="trigger-123",CLASS="twitch-trigger",START-DATE="2024-05-15T09:09:00.348Z",END-ON-NEXT=YES,X-TV-TWITCH-TRIGGER-URL="https://stream3.m3u8"
			#EXT-X-PROGRAM-DATE-TIME:2024-05-15T09:09:49.681Z
			#EXTINF:4.167,live
			https://video-edge-stream.test/chunk1.ts
			#EXT-X-PROGRAM-DATE-TIME:2024-05-15T09:09:53.848Z
			#EXTINF:4.167,live
			https://video-edge-stream.test/chunk2.ts
			#EXT-X-PROGRAM-DATE-TIME:2024-05-15T09:09:58.015Z
			#EXTINF:4.166,live
			%s
			""".formatted(M3U8_CHUNK_URL);
	
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
    
    @Test
    void getM3u8Url(UnirestMock unirest) throws MalformedURLException{
        unirest.expect(GET, M3U8_URL)
		        .queryString("sig", M3U8_SIGNATURE)
		        .queryString("token", M3U8_VALUE)
		        .queryString("cdm", "wv")
		        .queryString("player_version", "1.22.0")
		        .queryString("player_type", "pulsar")
		        .queryString("player_backend", "mediaplayer")
		        .queryString("playlist_include_framerate", "true")
		        .queryString("allow_source", "true")
		        .queryString("transcode_mode", "cbr_v1")
                .thenReturn(M3U8_BODY)
                .withStatus(200);
        
        assertThat(tested.getM3u8Url(CHANNEL_NAME, M3U8_SIGNATURE, M3U8_VALUE)).contains(URI.create("https://stream3.m3u8").toURL());
    }
    
    @Test
    void getM3u8UrlNoUrl(UnirestMock unirest){
        unirest.expect(GET, M3U8_URL)
		        .queryString("sig", M3U8_SIGNATURE)
		        .queryString("token", M3U8_VALUE)
		        .queryString("cdm", "wv")
		        .queryString("player_version", "1.22.0")
		        .queryString("player_type", "pulsar")
		        .queryString("player_backend", "mediaplayer")
		        .queryString("playlist_include_framerate", "true")
		        .queryString("allow_source", "true")
		        .queryString("transcode_mode", "cbr_v1")
                .thenReturn("")
                .withStatus(200);
        
        assertThat(tested.getM3u8Url(CHANNEL_NAME, M3U8_SIGNATURE, M3U8_VALUE)).isEmpty();
    }
    
    @Test
    void getM3u8UrlError(UnirestMock unirest){
        unirest.expect(GET, M3U8_URL)
		        .queryString("sig", M3U8_SIGNATURE)
		        .queryString("token", M3U8_VALUE)
		        .queryString("cdm", "wv")
		        .queryString("player_version", "1.22.0")
		        .queryString("player_type", "pulsar")
		        .queryString("player_backend", "mediaplayer")
		        .queryString("playlist_include_framerate", "true")
		        .queryString("allow_source", "true")
		        .queryString("transcode_mode", "cbr_v1")
                .thenReturn("")
                .withStatus(400);
        
        assertThat(tested.getM3u8Url(CHANNEL_NAME, M3U8_SIGNATURE, M3U8_VALUE)).isEmpty();
    }
    
    @Test
    void getM3u8UrlRegionLocked(UnirestMock unirest){
        unirest.expect(GET, M3U8_URL)
		        .queryString("sig", M3U8_SIGNATURE)
		        .queryString("token", M3U8_VALUE)
		        .queryString("cdm", "wv")
		        .queryString("player_version", "1.22.0")
		        .queryString("player_type", "pulsar")
		        .queryString("player_backend", "mediaplayer")
		        .queryString("playlist_include_framerate", "true")
		        .queryString("allow_source", "true")
		        .queryString("transcode_mode", "cbr_v1")
                .thenReturn()
                .withStatus(403);
        
        assertThat(tested.getM3u8Url(CHANNEL_NAME, M3U8_SIGNATURE, M3U8_VALUE)).isEmpty();
    }
	
	@Test
	void getM3u8ChunkUrl(UnirestMock unirest) throws MalformedURLException{
		var url = URI.create("https://stream.test/streamer.m3u8").toURL();
		
		unirest.expect(GET, url.toString())
				.thenReturn(M3U8_PLAYLIST_BODY)
				.withStatus(200);
		
		unirest.expect(GET, M3U8_CHUNK_URL)
				.thenReturn()
				.withStatus(200);
		
		assertThat(tested.openM3u8LastChunk(url)).isTrue();
	}
	
	@Test
	void getM3u8ChunkUrlNoPlaylist(UnirestMock unirest) throws MalformedURLException{
		var url = URI.create("https://stream.test/streamer.m3u8").toURL();
		
		unirest.expect(GET, url.toString())
				.thenReturn(M3U8_PLAYLIST_BODY)
				.withStatus(404);
		
		assertThat(tested.openM3u8LastChunk(url)).isFalse();
	}
	
	@Test
	void getM3u8ChunkUrlNoChunkUrl(UnirestMock unirest) throws MalformedURLException{
		var url = URI.create("https://stream.test/streamer.m3u8").toURL();
		
		unirest.expect(GET, url.toString())
				.thenReturn("")
				.withStatus(200);
		
		assertThat(tested.openM3u8LastChunk(url)).isFalse();
	}
	
	@Test
	void getM3u8ChunkUrlChunkError(UnirestMock unirest) throws MalformedURLException{
		var url = URI.create("https://stream.test/streamer.m3u8").toURL();
		
		unirest.expect(GET, url.toString())
				.thenReturn(M3U8_PLAYLIST_BODY)
				.withStatus(200);
		
		unirest.expect(GET, M3U8_CHUNK_URL)
				.thenReturn()
				.withStatus(400);
		
		assertThat(tested.openM3u8LastChunk(url)).isFalse();
	}
}