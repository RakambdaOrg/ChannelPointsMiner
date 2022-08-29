package fr.raksrinana.channelpointsminer.miner.api.ws;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.IPubSubMessage;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topics;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.response.ITwitchWebSocketResponse;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.response.MessageData;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.response.MessageResponse;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.factory.TwitchWebSocketClientFactory;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwitchPubSubWebSocketPoolTest{
	private static final Instant NOW = Instant.parse("2021-10-10T10:10:10Z");
	
	private final TwitchPubSubWebSocketPool tested = new TwitchPubSubWebSocketPool(50);
	
	@Mock
	private Topics topics;
	@Mock
	private Topic topic;
	@Mock
	private TwitchPubSubWebSocketClient client;
	@Mock
	private ITwitchWebSocketResponse twitchWebSocketResponse;
	@Mock
	private ITwitchPubSubMessageListener twitchMessageListener;
	
	@BeforeEach
	void setUp(){
		lenient().when(topics.getTopics()).thenReturn(Set.of(topic));
	}
	
	@Test
	void addTopicCreatesNewClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client);
			
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
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client);
			
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
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client);
			
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
			var client2 = mock(TwitchPubSubWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client).thenReturn(client2);
			
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
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client);
			
			doThrow(new RuntimeException("For tests")).when(client).connectBlocking();
			
			assertThrows(RuntimeException.class, () -> tested.listenTopic(topics));
			
			assertThat(tested.getClientCount()).isEqualTo(0);
		}
	}
	
	@Test
	void closesAllClients(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchPubSubWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client).thenReturn(client2);
			
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
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, NORMAL, "test", false));
			assertThat(tested.getClientCount()).isEqualTo(0);
		}
	}
	
	@Test
	void abnormalClientCloseRecreatesClient(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchPubSubWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client).thenReturn(client2);
			
			var topics = new Topics(topic);
			
			when(client.getTopics()).thenReturn(Set.of(topics));
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, ABNORMAL_CLOSE, "test", true));
			assertThat(tested.getClientCount()).isEqualTo(0);
			verify(client2, never()).listenTopic(topics);
		}
	}
	
	@Test
	void abnormalClientCloseRecreatesClient2(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchPubSubWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client).thenReturn(client2);
			
			var topics = new Topics(topic);
			
			when(client.getTopics()).thenReturn(Set.of(topics));
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, ABNORMAL_CLOSE, "test", true));
			assertThat(tested.getClientCount()).isEqualTo(0);
			verify(client2, never()).listenTopic(topics);
			
			tested.listenPendingTopics();
			assertThat(tested.getClientCount()).isEqualTo(1);
			verify(client2).listenTopic(topics);
		}
	}
	
	@Test
	void messagesAreRedirected(){
		var response = mock(MessageResponse.class);
		var data = mock(MessageData.class);
		var message = mock(IPubSubMessage.class);
		
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
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastPong()).thenReturn(NOW.minusSeconds(10));
			when(client.isOpen()).thenReturn(true);
			when(client.isClosing()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::ping);
			
			verify(client).ping();
		}
	}
	
	@Test
	void pingTimedOutClosing(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client);
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
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client);
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
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client);
			
			when(client.isTopicListened(topic)).thenReturn(true);
			tested.listenTopic(topics);
			
			tested.removeTopic(topic);
			verify(client).removeTopic(topic);
		}
	}
	
	@Test
	void removeUnknownTopic(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createPubSubClient).thenReturn(client);
			
			when(client.isTopicListened(topic)).thenReturn(false);
			tested.listenTopic(topics);
			
			tested.removeTopic(topic);
			verify(client, never()).removeTopic(topic);
		}
	}
}