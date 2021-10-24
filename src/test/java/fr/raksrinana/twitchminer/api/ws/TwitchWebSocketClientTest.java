package fr.raksrinana.twitchminer.api.ws;

import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.twitchminer.tests.WebsocketMockServer;
import fr.raksrinana.twitchminer.tests.WebsocketMockServerExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.time.Instant;
import java.util.Set;
import static fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName.VIDEO_PLAYBACK_BY_ID;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchWebSocketClientTest{
	private static final Instant NOW = Instant.parse("2021-02-25T15:25:36Z");
	
	private TwitchWebSocketClient tested;
	
	@Mock
	private TwitchWebSocketListener listener;
	
	@BeforeEach
	void setUp(){
		var uri = URI.create("ws://localhost:" + WebsocketMockServerExtension.PORT);
		tested = new TwitchWebSocketClient(uri);
		tested.addListener(listener);
	}
	
	@AfterEach
	void tearDown() throws InterruptedException{
		if(tested.isOpen()){
			tested.closeBlocking();
		}
	}
	
	@Test
	void connectSendsPing(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
		assertThat(server.getReceivedMessages()).contains("{\"type\":\"PING\"}");
	}
	
	@Test
	void pingSendsPing(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.reset();
		
		tested.ping();
		await().atMost(ofSeconds(5)).until(() -> server.getReceivedMessages().contains("{\"type\":\"PING\"}"));
	}
	
	@Test
	void addTopic(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		server.reset();
		
		var topic1 = Topic.builder()
				.name(VIDEO_PLAYBACK_BY_ID)
				.target("target1")
				.build();
		var topic2 = Topic.builder()
				.name(VIDEO_PLAYBACK_BY_ID)
				.target("target2")
				.build();
		
		tested.listenTopic(Topics.builder()
				.topics(Set.of(topic1))
				.build());
		
		assertThat(tested.getTopicCount()).isEqualTo(1);
		assertThat(tested.isTopicListened(topic1)).isTrue();
		assertThat(tested.isTopicListened(topic2)).isFalse();
		
		tested.listenTopic(Topics.builder()
				.topics(Set.of(topic2))
				.build());
		
		assertThat(tested.getTopicCount()).isEqualTo(2);
		assertThat(tested.isTopicListened(topic1)).isTrue();
		assertThat(tested.isTopicListened(topic2)).isTrue();
		
		tested.listenTopic(Topics.builder()
				.topics(Set.of(topic2))
				.build());
		
		assertThat(tested.getTopicCount()).isEqualTo(2);
		assertThat(tested.isTopicListened(topic1)).isTrue();
		assertThat(tested.isTopicListened(topic2)).isTrue();
	}
	
	@Test
	void onBadMessage(){
		assertDoesNotThrow(() -> tested.onMessage("invalid"));
		
		verify(listener, never()).onWebSocketMessage(any());
	}
	
	@Test
	void closeCallsListener() throws InterruptedException{
		tested.connectBlocking();
		tested.closeBlocking();
		
		verify(listener).onWebSocketClosed(eq(tested), anyInt(), any(String.class), anyBoolean());
	}
}