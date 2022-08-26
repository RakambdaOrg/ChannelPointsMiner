package fr.raksrinana.channelpointsminer.miner.api.chat.ws;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatMessageListener;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.WebsocketMockServer;
import fr.raksrinana.channelpointsminer.miner.tests.WebsocketMockServerExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchChatWebSocketClientLeaveJoinTest{
	private static final String USERNAME = "USERNAME";
	private static final String ACCESS_TOKEN = "token";
	
	private TwitchChatWebSocketClient tested;
	
	@Mock
	private ITwitchChatWebSocketClosedListener listener;
	@Mock
	private TwitchLogin twitchLogin;
    @Mock
    private ITwitchChatMessageListener chatMessageListener;
    private final List<ITwitchChatMessageListener> chatMessageListeners = Arrays.asList(chatMessageListener, chatMessageListener);
    
	@AfterEach
	void tearDown(WebsocketMockServer server){
		tested.close();
		server.removeClients();
	}
	
	@Test
	void joinLeaveChannel(WebsocketMockServer server){
		var streamer = "streamer";
		
		assertThat(tested.getChannelCount()).isEqualTo(0L);
		assertThat(tested.getChannels()).isEmpty();
		assertThat(tested.isChannelJoined(streamer)).isFalse();
		
		tested.join(streamer);
		server.awaitMessage();
		assertThat(server.getReceivedMessages()).contains("JOIN #%s".formatted(streamer));
		assertThat(tested.getChannelCount()).isEqualTo(1L);
		assertThat(tested.getChannels()).containsExactly(streamer);
		assertThat(tested.isChannelJoined(streamer)).isTrue();
		server.reset();
		
		tested.join(streamer);
		server.awaitNothing();
		assertThat(tested.getChannelCount()).isEqualTo(1L);
		assertThat(tested.getChannels()).containsExactly(streamer);
		assertThat(tested.isChannelJoined(streamer)).isTrue();
		server.reset();
		
		tested.leave(streamer);
		server.awaitMessage();
		assertThat(server.getReceivedMessages()).contains("PART #%s".formatted(streamer));
		assertThat(tested.getChannelCount()).isEqualTo(0L);
		assertThat(tested.getChannels()).isEmpty();
		assertThat(tested.isChannelJoined(streamer)).isFalse();
		server.reset();
		
		tested.leave(streamer);
		server.awaitNothing();
		assertThat(tested.getChannelCount()).isEqualTo(0L);
		assertThat(tested.getChannels()).isEmpty();
		assertThat(tested.isChannelJoined(streamer)).isFalse();
		server.reset();
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server) throws InterruptedException{
		when(twitchLogin.getUsername()).thenReturn(USERNAME);
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
		
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchChatWebSocketClient(uri, twitchLogin, chatMessageListeners);
		tested.setReuseAddr(true);
		tested.addWebSocketClosedListener(listener);
		tested.connectBlocking();
		server.awaitMessage(3);
		server.reset();
	}
}