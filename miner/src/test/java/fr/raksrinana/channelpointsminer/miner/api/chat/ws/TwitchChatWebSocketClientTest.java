package fr.raksrinana.channelpointsminer.miner.api.chat.ws;

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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchChatWebSocketClientTest{
	private static final int MESSAGE_TIMEOUT = 15000;
	private static final String USERNAME = "USERNAME";
	private static final String ACCESS_TOKEN = "token";
	
	private TwitchChatWebSocketClient tested;
	
	@Mock
	private ITwitchChatWebSocketListener listener;
	@Mock
	private TwitchLogin twitchLogin;
	
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
	
	@BeforeEach
	void setUp(WebsocketMockServer server) throws InterruptedException{
		lenient().when(twitchLogin.getUsername()).thenReturn(USERNAME);
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
		
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchChatWebSocketClient(uri, twitchLogin);
		tested.setReuseAddr(true);
		tested.addListener(listener);
	}
}