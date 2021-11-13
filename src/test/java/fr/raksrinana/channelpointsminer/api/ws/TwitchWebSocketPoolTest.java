package fr.raksrinana.channelpointsminer.api.ws;

import fr.raksrinana.channelpointsminer.api.ws.data.message.Message;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.channelpointsminer.api.ws.data.response.MessageData;
import fr.raksrinana.channelpointsminer.api.ws.data.response.MessageResponse;
import fr.raksrinana.channelpointsminer.api.ws.data.response.TwitchWebSocketResponse;
import fr.raksrinana.channelpointsminer.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.factory.TwitchWebSocketClientFactory;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.java_websocket.framing.CloseFrame.ABNORMAL_CLOSE;
import static org.java_websocket.framing.CloseFrame.NORMAL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwitchWebSocketPoolTest{
	private static final Instant NOW = Instant.parse("2021-10-10T10:10:10Z");
	
	private final TwitchWebSocketPool tested = new TwitchWebSocketPool(50);
	
	@Mock
	private Topics topics;
	@Mock
	private Topic topic;
	@Mock
	private TwitchWebSocketClient client;
	@Mock
	private TwitchWebSocketResponse twitchWebSocketResponse;
	@Mock
	private TwitchMessageListener twitchMessageListener;
	
	@BeforeEach
	void setUp(){
		lenient().when(topics.getTopics()).thenReturn(Set.of(topic));
	}
	
	@Test
	void addTopicCreatesNewClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client).listenTopic(topics);
		}
	}
	
	@Test
	void addNewTopicToExistingClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			when(client.isTopicListened(topic)).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client, times(2)).listenTopic(topics);
		}
	}
	
	@Test
	void addExistingTopicToExistingClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			when(client.isTopicListened(topic)).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client).listenTopic(topics);
		}
	}
	
	@Test
	void manyTopicsAreSplitOnSeveralClients(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client).thenReturn(client2);
			
			when(client.isTopicListened(any())).thenReturn(false);
			when(client.getTopicCount()).thenReturn(0);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			when(client.getTopicCount()).thenReturn(50);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			
			assertThat(tested.getClientCount()).isEqualTo(2);
		}
	}
	
	@Test
	void clientError() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			doThrow(new RuntimeException("For tests")).when(client).connectBlocking();
			
			assertThrows(RuntimeException.class, () -> tested.listenTopic(topics));
			
			assertThat(tested.getClientCount()).isEqualTo(0);
		}
	}
	
	@Test
	void closesAllClients(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client).thenReturn(client2);
			
			when(client.isTopicListened(any())).thenReturn(false);
			when(client.getTopicCount()).thenReturn(0);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			when(client.getTopicCount()).thenReturn(50);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			
			assertDoesNotThrow(tested::close);
			
			verify(client).close();
			verify(client2).close();
		}
	}
	
	@Test
	void normalClientCloseRemovesClient(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, NORMAL, "test", false));
			assertThat(tested.getClientCount()).isEqualTo(0);
		}
	}
	
	@Test
	void abnormalClientCloseRecreatesClient(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client).thenReturn(client2);
			
			var topics = new Topics(topic);
			
			when(client.getTopics()).thenReturn(Set.of(topics));
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, ABNORMAL_CLOSE, "test", true));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client2).listenTopic(topics);
		}
	}
	
	@Test
	void messagesAreRedirected(){
		var response = mock(MessageResponse.class);
		var data = mock(MessageData.class);
		var message = mock(Message.class);
		
		when(response.getData()).thenReturn(data);
		when(data.getMessage()).thenReturn(message);
		when(data.getTopic()).thenReturn(topic);
		
		assertDoesNotThrow(() -> tested.addListener(twitchMessageListener));
		assertDoesNotThrow(() -> tested.onWebSocketMessage(twitchWebSocketResponse));
		assertDoesNotThrow(() -> tested.onWebSocketMessage(response));
		
		verify(twitchMessageListener).onTwitchMessage(topic, message);
	}
	
	@Test
	void pingSendsPing(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastPong()).thenReturn(NOW.minusSeconds(10));
			when(client.isOpen()).thenReturn(true);
			when(client.isClosing()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::ping);
		}
	}
	
	@Test
	void pingTimedOutClosing(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastPong()).thenReturn(NOW.minusSeconds(600));
			when(client.isOpen()).thenReturn(true);
			when(client.isClosing()).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::ping);
			
			verify(client).close(eq(ABNORMAL_CLOSE), anyString());
		}
	}
	
	@Test
	void pingTimedOutClosed(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastPong()).thenReturn(NOW.minusSeconds(600));
			when(client.isOpen()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::ping);
			
			verify(client).close(eq(ABNORMAL_CLOSE), anyString());
		}
	}
	
	@Test
	void removeTopic(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			when(client.isTopicListened(topic)).thenReturn(true);
			tested.listenTopic(topics);
			
			tested.removeTopic(topic);
			verify(client).removeTopic(topic);
		}
	}
	
	@Test
	void removeUnknownTopic(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			when(client.isTopicListened(topic)).thenReturn(false);
			tested.listenTopic(topics);
			
			tested.removeTopic(topic);
			verify(client, never()).removeTopic(topic);
		}
	}
}