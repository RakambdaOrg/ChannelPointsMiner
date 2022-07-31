package fr.raksrinana.channelpointsminer.miner.api.chat.ws;

import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.factory.TwitchWebSocketClientFactory;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.java_websocket.framing.CloseFrame.ABNORMAL_CLOSE;
import static org.java_websocket.framing.CloseFrame.NORMAL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Answers.RETURNS_DEFAULTS;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwitchChatWebSocketPoolTest{
	private static final Instant NOW = Instant.parse("2021-10-10T10:10:10Z");
	private static final String STREAMER = "STREAMER";
	private static final String STREAMER_LOWER = STREAMER.toLowerCase(Locale.ROOT);
	
	private TwitchChatWebSocketPool tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@Mock
	private TwitchChatWebSocketClient client;
	
	@BeforeEach
	void setUp(){
		tested = new TwitchChatWebSocketPool(50, twitchLogin);
	}
	
	@Test
	void addChannelCreatesNewClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client).join(STREAMER_LOWER);
		}
	}
	
	@Test
	void addNewChannelToExistingClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			
			when(client.isChannelJoined(STREAMER_LOWER)).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client, times(2)).join(STREAMER_LOWER);
		}
	}
	
	@Test
	void addExistingChannelToExistingClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			
			when(client.isChannelJoined(STREAMER_LOWER)).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client).join(STREAMER_LOWER);
		}
	}
	
	@Test
	void clientError() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			
			doThrow(new RuntimeException("For tests")).when(client).connectBlocking();
			
			assertThrows(RuntimeException.class, () -> tested.join(STREAMER));
			
			assertThat(tested.getClientCount()).isEqualTo(0);
		}
	}
	
	@Test
	void clientErrorJoinPending() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			
			doThrow(new RuntimeException("For tests")).when(client).connectBlocking();
			
			assertThrows(RuntimeException.class, () -> tested.join(STREAMER));
			
			assertThat(tested.getClientCount()).isEqualTo(0);
			
			doAnswer(RETURNS_DEFAULTS).when(client).connectBlocking();
			assertDoesNotThrow(() -> tested.join(STREAMER));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client, times(2)).addListener(tested);
			verify(client, times(2)).connectBlocking();
			verify(client).join(STREAMER_LOWER);
		}
	}
	
	@Test
	void normalClientCloseRemovesClient(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, NORMAL, "test", false));
			assertThat(tested.getClientCount()).isEqualTo(0);
		}
	}
	
	@Test
	void abnormalClientCloseRecreatesClient(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchChatWebSocketClient.class);
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client).thenReturn(client2);
			
			when(client.getChannels()).thenReturn(Set.of(STREAMER));
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, ABNORMAL_CLOSE, "test", true));
			tested.joinPending();
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client2).join(STREAMER_LOWER);
		}
	}
	
	@Test
	void pingSendsPing(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastHeartbeat()).thenReturn(NOW.minusSeconds(10));
			when(client.isOpen()).thenReturn(true);
			when(client.isClosing()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.join("test"));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::ping);
			
			verify(client).ping();
		}
	}
	
	@Test
	void closeTimedOut(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastHeartbeat()).thenReturn(NOW.minusSeconds(600));
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::checkStaleConnection);
			
			verify(client).close(eq(ABNORMAL_CLOSE), anyString());
		}
	}
	
	@Test
	void keepNotTimedOut(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastHeartbeat()).thenReturn(NOW.minusSeconds(300));
			
			assertDoesNotThrow(() -> tested.join(STREAMER));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::checkStaleConnection);
			
			verify(client, never()).close(anyInt(), anyString());
		}
	}
	
	@Test
	void removeChannel(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			
			when(client.isChannelJoined(STREAMER_LOWER)).thenReturn(true);
			tested.join(STREAMER);
			
			tested.leave(STREAMER);
			verify(client).leave(STREAMER_LOWER);
		}
	}
	
	@Test
	void removeUnknownChannel(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			
			when(client.isChannelJoined(STREAMER_LOWER)).thenReturn(false);
			tested.join(STREAMER);
			
			tested.leave(STREAMER);
			verify(client, never()).leave(STREAMER_LOWER);
		}
	}
	
	@Test
	void closeClosesClients(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(() -> TwitchWebSocketClientFactory.createChatClient(twitchLogin)).thenReturn(client);
			
			tested.join(STREAMER);
			assertDoesNotThrow(() -> tested.close());
			
			verify(client).close();
		}
	}
}