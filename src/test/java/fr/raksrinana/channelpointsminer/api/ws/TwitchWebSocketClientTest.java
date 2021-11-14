package fr.raksrinana.channelpointsminer.api.ws;

import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.channelpointsminer.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.tests.WebsocketMockServer;
import fr.raksrinana.channelpointsminer.tests.WebsocketMockServerExtension;
import org.java_websocket.framing.Framedata;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.time.Instant;
import static fr.raksrinana.channelpointsminer.api.ws.data.request.topic.TopicName.VIDEO_PLAYBACK_BY_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchWebSocketClientTest{
	private static final Instant NOW = Instant.parse("2021-10-15T15:30:14.014Z");
	
	private TwitchWebSocketClient tested;
	
	@Mock
	private ITwitchWebSocketListener listener;
	
	@BeforeEach
	void setUp(WebsocketMockServer server){
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchWebSocketClient(uri);
		tested.addListener(listener);
	}
	
	@AfterEach
	void tearDown(WebsocketMockServer server){
		tested.close();
		server.removeClients();
	}
	
	@Test
	void connectSendsPing(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage();
		
		assertThat(server.getReceivedMessages()).contains("{\"type\":\"PING\"}");
	}
	
	@Test
	void pingSendsPing(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage();
		server.reset();
		
		tested.ping();
		server.awaitMessage();
		assertThat(server.getReceivedMessages()).contains("{\"type\":\"PING\"}");
	}
	
	@Test
	void addTopic(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.awaitMessage();
		server.reset();
		
		var topic1 = Topic.builder()
				.name(VIDEO_PLAYBACK_BY_ID)
				.target("target1")
				.build();
		var topic2 = Topic.builder()
				.name(VIDEO_PLAYBACK_BY_ID)
				.target("target2")
				.build();
		
		tested.listenTopic(new Topics(topic1, null));
		server.awaitMessage();
		
		assertThat(tested.getTopicCount()).isEqualTo(1);
		assertThat(tested.isTopicListened(topic1)).isTrue();
		assertThat(tested.isTopicListened(topic2)).isFalse();
		assertThat(server.getReceivedMessages()).hasSize(1);
		
		server.reset();
		tested.listenTopic(new Topics(topic2));
		server.awaitMessage();
		
		assertThat(tested.getTopicCount()).isEqualTo(2);
		assertThat(tested.isTopicListened(topic1)).isTrue();
		assertThat(tested.isTopicListened(topic2)).isTrue();
		assertThat(server.getReceivedMessages()).hasSize(1);
		
		server.reset();
		tested.listenTopic(new Topics(topic2));
		server.awaitNothing();
		
		assertThat(tested.getTopicCount()).isEqualTo(2);
		assertThat(tested.isTopicListened(topic1)).isTrue();
		assertThat(tested.isTopicListened(topic2)).isTrue();
		assertThat(server.getReceivedMessages()).isEmpty();
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
		server.awaitMessage();
		server.reset();
		
		var topic1 = Topic.builder()
				.name(VIDEO_PLAYBACK_BY_ID)
				.target("target1")
				.build();
		var topic2 = Topic.builder()
				.name(VIDEO_PLAYBACK_BY_ID)
				.target("target2")
				.build();
		
		tested.listenTopic(new Topics(topic1, null));
		server.awaitMessage();
		assertThat(tested.getTopicCount()).isEqualTo(1);
		assertThat(server.getReceivedMessages()).hasSize(1);
		
		server.reset();
		tested.removeTopic(topic2);
		server.awaitNothing();
		assertThat(tested.getTopicCount()).isEqualTo(1);
		assertThat(server.getReceivedMessages()).isEmpty();
		
		tested.removeTopic(topic1);
		server.awaitMessage();
		assertThat(tested.getTopicCount()).isEqualTo(0);
		assertThat(server.getReceivedMessages()).hasSize(1);
		
		server.reset();
		tested.removeTopic(topic1);
		server.awaitNothing();
		assertThat(tested.getTopicCount()).isEqualTo(0);
		assertThat(server.getReceivedMessages()).isEmpty();
	}
}