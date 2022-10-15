package fr.raksrinana.channelpointsminer.miner.api.chat.ws;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatMessageListener;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.WebsocketMockServer;
import fr.raksrinana.channelpointsminer.miner.tests.WebsocketMockServerExtension;
import org.java_websocket.framing.Framedata;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchChatWebSocketClientTest{
	private static final String USERNAME = "USERNAME";
	private static final String ACCESS_TOKEN = "token";
	private static final String STREAMER = "streamer";
	private static final String STREAMER_CHANNEL = "#streamer";
	private static final String BADGE_INFO = "badge/info";
	private static final String MESSAGE = "message";
	
	private static final String MESSAGE_PAYLOAD =
			"@badge-info=;badges=" + BADGE_INFO + ";client-nonce=0;color=0;display-name=" + USERNAME + ";" +
					"emotes=0;first-msg=0;flags=;id=id;mod=0;returning-chatter=0;room-id=room;subscriber=0;tmi-sent-ts=0;turbo=0;" +
					"user-id=userid;user-type= :usertype.tmi.twitch.tv PRIVMSG " +
					STREAMER_CHANNEL + " :" + MESSAGE;
	
	private TwitchChatWebSocketClient tested;
	
	@Mock
	private ITwitchChatWebSocketClosedListener listener;
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private ITwitchChatMessageListener chatMessageListener;
	
	@Test
	void onMessageChatMessageWithoutChatMonitored(){
		tested.onMessage(MESSAGE_PAYLOAD);
		
		verify(chatMessageListener, never()).onChatMessage(any(), any(), any());
		verify(chatMessageListener, never()).onChatMessage(any(), any(), any(), any());
	}
	
	@AfterEach
	void tearDown(WebsocketMockServer server){
		tested.close();
		server.removeClients();
	}
	
	@Test
	void connect(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage(3);
		
		assertThat(server.getReceivedMessages()).contains(
				"CAP REQ :twitch.tv/tags twitch.tv/commands",
				"PASS oauth:%s".formatted(ACCESS_TOKEN),
				"NICK %s".formatted(USERNAME.toLowerCase())
		);
		
		assertThat(tested.getUuid()).isNotNull();
	}
	
	@Test
	void onError(){
		assertDoesNotThrow(() -> tested.onError(new RuntimeException("For tests")));
	}
	
	@Test
	void closeCallsListener(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		tested.closeBlocking();
		verify(listener).onWebSocketClosed(eq(tested), anyInt(), any(String.class), anyBoolean());
	}
	
	@Test
	void pingUpdatesHeartbeat(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage(3);
		server.reset();
		
		var now = Instant.now();
		Thread.sleep(10);
		server.sendPing();
		server.awaitMessage();
		
		assertThat(server.getReceivedMessages()).contains("PONG");
		assertThat(tested.getLastHeartbeat()).isAfter(now);
	}
	
	@Test
	void pongUpdatesHeartbeat(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage(3);
		server.reset();
		
		var now = Instant.now();
		Thread.sleep(100);
		
		tested.onWebsocketPong(tested, mock(Framedata.class));
		
		assertThat(tested.getLastHeartbeat()).isAfter(now);
	}
	
	@Test
	void pongUpdatesHeartbeat2(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage(3);
		server.reset();
		
		var now = Instant.now();
		Thread.sleep(100);
		
		server.send("PONG :tmi.twitch.tv");
		
		await().atMost(Duration.ofSeconds(30)).until(() -> tested.getLastHeartbeat().isAfter(now));
	}
	
	@Test
	void sendPing(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage(3);
		server.reset();
		
		tested.ping();
		server.awaitMessage();
		
		assertThat(server.getReceivedMessages()).contains("PING");
	}
	
	@Test
	void onMessageChatMessageListenerCalled(){
		tested.setListenMessages(true);
		tested.addChatMessageListener(chatMessageListener);
		
		tested.onMessage(MESSAGE_PAYLOAD);
		
		verify(chatMessageListener).onChatMessage(STREAMER, USERNAME, MESSAGE, BADGE_INFO);
	}
	
	@Test
	void onMessageException(){
		assertDoesNotThrow(() -> tested.onMessage((String) null)); //This is theoretically impossible
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server){
		lenient().when(twitchLogin.getUsername()).thenReturn(USERNAME);
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
		
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchChatWebSocketClient(uri, twitchLogin, false);
		tested.setReuseAddr(true);
		tested.addWebSocketClosedListener(listener);
	}
}