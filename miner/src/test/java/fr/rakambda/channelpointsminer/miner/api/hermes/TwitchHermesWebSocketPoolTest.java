package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.SubscribeRequest;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.ITwitchHermesWebSocketResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.NotificationResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.notification.NotificationData;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.notification.PubSubNotificationType;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.pubsub.ITwitchPubSubMessageListener;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topics;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.factory.TwitchWebSocketClientFactory;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.java_websocket.framing.CloseFrame.ABNORMAL_CLOSE;
import static org.java_websocket.framing.CloseFrame.NORMAL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwitchHermesWebSocketPoolTest{
	private static final Instant NOW = Instant.parse("2021-10-10T10:10:10Z");
	private static final String SUBSCRIBED_TOPIC_ID = "id1";
	
	private TwitchHermesWebSocketPool tested;
	
	@Mock
	private Topic topic;
	@Mock
	private TwitchHermesWebSocketClient client;
	@Mock
	private ITwitchHermesWebSocketResponse twitchWebSocketResponse;
	@Mock
	private ITwitchPubSubMessageListener twitchMessageListener;
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		tested = new TwitchHermesWebSocketPool(50, twitchLogin);
		
		lenient().when(client.listenPubSubTopic(any())).thenReturn(Optional.of(SUBSCRIBED_TOPIC_ID));
	}
	
	@Test
	void addTopicCreatesNewClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client).listenPubSubTopic(topic);
		}
	}
	
	@Test
	void addNewTopicToExistingClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client);
			
			when(client.isPubSubTopicListened(topic)).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client, times(2)).listenPubSubTopic(topic);
		}
	}
	
	@Test
	void addExistingTopicToExistingClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client);
			
			when(client.isPubSubTopicListened(topic)).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client).listenPubSubTopic(topic);
		}
	}
	
	@Test
	void manyTopicsAreSplitOnSeveralClients(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchHermesWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client).thenReturn(client2);
			
			when(client.isPubSubTopicListened(any())).thenReturn(false);
			when(client.getSubscriptionCount()).thenReturn(0);
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			when(client.getSubscriptionCount()).thenReturn(50);
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			
			assertThat(tested.getClientCount()).isEqualTo(2);
		}
	}
	
	@Test
	void clientError() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client);
			
			doThrow(new RuntimeException("For tests")).when(client).connectBlocking();
			
			assertThrows(RuntimeException.class, () -> tested.listenPubSubTopic(topic));
			
			assertThat(tested.getClientCount()).isEqualTo(0);
		}
	}
	
	@Test
	void closesAllClients(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchHermesWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client).thenReturn(client2);
			
			when(client.isPubSubTopicListened(any())).thenReturn(false);
			when(client.getSubscriptionCount()).thenReturn(0);
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			when(client.getSubscriptionCount()).thenReturn(50);
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			
			assertDoesNotThrow(tested::close);
			
			verify(client).close();
			verify(client2).close();
		}
	}
	
	@Test
	void normalClientCloseRemovesClient(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, NORMAL, "test", false));
			assertThat(tested.getClientCount()).isEqualTo(0);
		}
	}
	
	@Test
	void abnormalClientCloseRecreatesClient(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchHermesWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client).thenReturn(client2);
			
			when(client.getSubscribeRequests()).thenReturn(Map.of(SUBSCRIBED_TOPIC_ID, mock(SubscribeRequest.class)));
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, ABNORMAL_CLOSE, "test", true));
			assertThat(tested.getClientCount()).isEqualTo(0);
			verify(client2, never()).listenPubSubTopic(topic);
		}
	}
	
	@Test
	void abnormalClientCloseRecreatesClient2(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchHermesWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client).thenReturn(client2);
			
			var topics = new Topics(topic);
			
			when(client.getSubscribeRequests()).thenReturn(Map.of(SUBSCRIBED_TOPIC_ID, mock(SubscribeRequest.class)));
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, ABNORMAL_CLOSE, "test", true));
			assertThat(tested.getClientCount()).isEqualTo(0);
			verify(client2, never()).listenPubSubTopic(topic);
			
			tested.listenPendingPubSubTopics();
			assertThat(tested.getClientCount()).isEqualTo(1);
			verify(client2).listenPubSubTopic(topic);
		}
	}
	
	@Test
	void messagesAreRedirected(){
		try(var jacksonUtils = Mockito.mockStatic(JacksonUtils.class);
				var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var message = mock(IPubSubMessage.class);
			
			jacksonUtils.when(() -> JacksonUtils.read(eq("test"), any())).thenReturn(message);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client);
			
			tested.listenPubSubTopic(topic);
			
			var response = mock(NotificationResponse.class);
			var data = mock(PubSubNotificationType.class);
			var subscription = mock(NotificationData.Subscription.class);
			
			when(response.getNotification()).thenReturn(data);
			when(data.getPubsub()).thenReturn("test");
			when(data.getSubscription()).thenReturn(subscription);
			when(subscription.getId()).thenReturn(SUBSCRIBED_TOPIC_ID);
			
			assertDoesNotThrow(() -> tested.addPubSubListener(twitchMessageListener));
			assertDoesNotThrow(() -> tested.onWebSocketMessage(twitchWebSocketResponse));
			assertDoesNotThrow(() -> tested.onWebSocketMessage(response));
			
			verify(twitchMessageListener).onTwitchMessage(topic, message);
		}
	}
	
	@Test
	void pingTimedOutClosing(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastPong()).thenReturn(NOW.minusSeconds(600));
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::ping);
			
			verify(client).close(eq(ABNORMAL_CLOSE), anyString());
		}
	}
	
	@Test
	void pingTimedOutClosed(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastPong()).thenReturn(NOW.minusSeconds(600));
			
			assertDoesNotThrow(() -> tested.listenPubSubTopic(topic));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::ping);
			
			verify(client).close(eq(ABNORMAL_CLOSE), anyString());
		}
	}
	
	@Test
	void removeTopic(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client);
			
			when(client.isPubSubTopicListened(topic)).thenReturn(true);
			tested.listenPubSubTopic(topic);
			
			tested.removePubSubTopic(topic);
			verify(client).removeSubscription(SUBSCRIBED_TOPIC_ID);
		}
	}
	
	@Test
	void removeUnknownTopic(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createHermesClient).thenReturn(client);
			
			when(client.isPubSubTopicListened(topic)).thenReturn(false);
			tested.listenPubSubTopic(topic);
			
			tested.removePubSubTopic(topic);
			verify(client, never()).removeSubscription(anyString());
		}
	}
}