package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.tests.WebsocketMockServer;
import fr.rakambda.channelpointsminer.miner.tests.WebsocketMockServerExtension;
import org.java_websocket.framing.Framedata;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.time.Instant;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.VIDEO_PLAYBACK_BY_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchHermesWebSocketClientTest{
	private static final Instant NOW = Instant.parse("2021-10-15T15:30:14.014Z");
	private static final String ACCESS_TOKEN = "token";
	
	private TwitchHermesWebSocketClient tested;
	
	@Mock
	private ITwitchHermesWebSocketListener listener;
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@AfterEach
	void tearDown(WebsocketMockServer server){
		tested.close();
		server.removeClients();
	}
	
	@Test
	void addTopic(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
		var topic1 = Topic.builder()
				.name(VIDEO_PLAYBACK_BY_ID)
				.target("target1")
				.build();
		var topic2 = Topic.builder()
				.name(VIDEO_PLAYBACK_BY_ID)
				.target("target2")
				.build();
		
		tested.listenPubSubTopic(topic1);
		server.awaitMessage();
		
		assertThat(tested.getSubscriptionCount()).isEqualTo(1);
		assertThat(tested.isPubSubTopicListened(topic1)).isTrue();
		assertThat(tested.isPubSubTopicListened(topic2)).isFalse();
		assertThat(server.getReceivedMessages()).hasSize(1);
		
		server.reset();
		tested.listenPubSubTopic(topic2);
		server.awaitMessage();
		
		assertThat(tested.getSubscriptionCount()).isEqualTo(2);
		assertThat(tested.isPubSubTopicListened(topic1)).isTrue();
		assertThat(tested.isPubSubTopicListened(topic2)).isTrue();
		assertThat(server.getReceivedMessages()).hasSize(1);
	}
	
	@Test
	void onBadMessage(){
		assertDoesNotThrow(() -> tested.onMessage("invalid"));
		
		verify(listener, never()).onWebSocketMessage(any());
	}
	
	@Test
	void onError(){
		assertDoesNotThrow(() -> tested.onError(new RuntimeException("For tests")));
	}
	
	@Test
	void onWsPong(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			var frame = mock(Framedata.class);
			
			tested.onWebsocketPong(tested, frame);
			
			assertThat(tested.getLastPong()).isEqualTo(NOW);
		}
	}
	
	@Test
	void closeCallsListener() throws InterruptedException{
		tested.connectBlocking();
		tested.closeBlocking();
		
		verify(listener).onWebSocketClosed(eq(tested), anyInt(), any(String.class), anyBoolean());
	}
	
	@Test
	void removeTopic(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
		var topic1 = Topic.builder()
				.name(VIDEO_PLAYBACK_BY_ID)
				.target("target1")
				.build();
		
		var id1 = tested.listenPubSubTopic(topic1).orElseThrow();
		server.awaitMessage();
		assertThat(server.getReceivedMessages()).hasSize(1);
		
		server.reset();
		tested.removeSubscription("id2");
		server.awaitNothing();
		assertThat(server.getReceivedMessages()).isEmpty();
		
		tested.removeSubscription(id1);
		server.awaitMessage();
		assertThat(server.getReceivedMessages()).hasSize(1);
	}
	
	@Test
	void authenticate(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		tested.authenticate(twitchLogin);
		
		server.awaitMessage();
		assertThat(server.getReceivedMessages()).hasSize(1);
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server){
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchHermesWebSocketClient(uri);
		tested.setReuseAddr(true);
		tested.addListener(listener);
	}
}