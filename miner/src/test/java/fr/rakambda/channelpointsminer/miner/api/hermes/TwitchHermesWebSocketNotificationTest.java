package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.NotificationResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.notification.NotificationData;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.notification.PubSubNotificationType;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PointsEarned;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.pointsearned.Balance;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.pointsearned.PointsEarnedData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.PointGain;
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
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchHermesWebSocketNotificationTest{
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
		
		server.send(TestUtils.getAllResourceContent("api/hermes/notification_ok.json"));
		
		var expected = NotificationResponse.builder()
				.id("4ae4865b-cedc-4755-8335-0560b7d05341")
				.timestamp(ZonedDateTime.parse("2025-01-02T03:04:05.123456789Z"))
				.notification(PubSubNotificationType.builder()
						.subscription(NotificationData.Subscription.builder()
								.id("id1")
								.build())
						.pubsub(PointsEarned.builder()
								.data(PointsEarnedData.builder()
										.timestamp(ZonedDateTime.parse("2025-01-02T03:04:05.123456789Z"))
										.channelId("123456789")
										.pointGain(PointGain.builder()
												.reasonCode("WATCH")
												.totalPoints(10)
												.build())
										.balance(Balance.builder()
												.channelId("123456789")
												.balance(50)
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server){
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchHermesWebSocketClient(uri, eventManager);
		tested.setReuseAddr(true);
		tested.addListener(listener);
	}
}