package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.UnsubscribeResponse;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.tests.TestUtils;
import fr.rakambda.channelpointsminer.miner.tests.WebsocketMockServer;
import fr.rakambda.channelpointsminer.miner.tests.WebsocketMockServerExtension;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.awaitility.core.ConditionTimeoutException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchHermesWebSocketUnsubscribeTest{
	private static final int MESSAGE_TIMEOUT = 15000;
	private TwitchHermesWebSocketClient tested;
	
	@Mock
	private ITwitchHermesWebSocketListener listener;
	@Mock
	private IEventManager eventManager;
	
	@AfterEach
	void tearDown(WebsocketMockServer server){
		tested.close();
		server.removeClients();
	}
	
	@RepeatedIfExceptionsTest(repeats = 5, exceptions = ConditionTimeoutException.class)
	void onResponse(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
		var id1 = tested.listenPubSubTopic(Topic.builder().name(TopicName.ONSITE_NOTIFICATIONS).target("123456789").build()).orElseThrow();
		assertThat(tested.getSubscriptionCount()).isEqualTo(1);
		
		server.send(TestUtils.getAllResourceContent("api/hermes/unsubscribe_ok.json").replace("id1", id1));
		
		var expected = UnsubscribeResponse.builder()
				.id("4ae4865b-cedc-4755-8335-0560b7d05341")
				.parentId("11a0ae22-6929-4b26-84b3-92dbbd9c710e")
				.timestamp(ZonedDateTime.parse("2025-01-02T03:04:05.123456789Z"))
				.unsubscribeResponse(UnsubscribeResponse.UnsubscribeResponseData.builder()
						.result("ok")
						.subscription(UnsubscribeResponse.SubscriptionData.builder()
								.id(id1)
								.build())
						.build())
				.build();
		
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
		assertThat(tested.getSubscriptionCount()).isEqualTo(0);
	}
	
	@RepeatedIfExceptionsTest(repeats = 5, exceptions = ConditionTimeoutException.class)
	void onResponseUnknownSubscription(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
		tested.listenPubSubTopic(Topic.builder().name(TopicName.ONSITE_NOTIFICATIONS).target("123456789").build()).orElseThrow();
		assertThat(tested.getSubscriptionCount()).isEqualTo(1);
		
		server.send(TestUtils.getAllResourceContent("api/hermes/unsubscribe_ok.json"));
		
		var expected = UnsubscribeResponse.builder()
				.id("4ae4865b-cedc-4755-8335-0560b7d05341")
				.parentId("11a0ae22-6929-4b26-84b3-92dbbd9c710e")
				.timestamp(ZonedDateTime.parse("2025-01-02T03:04:05.123456789Z"))
				.unsubscribeResponse(UnsubscribeResponse.UnsubscribeResponseData.builder()
						.result("ok")
						.subscription(UnsubscribeResponse.SubscriptionData.builder()
								.id("id1")
								.build())
						.build())
				.build();
		
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
		assertThat(tested.getSubscriptionCount()).isEqualTo(1);
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server){
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchHermesWebSocketClient(uri, eventManager);
		tested.setReuseAddr(true);
		tested.addListener(listener);
	}
}