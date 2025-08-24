package fr.rakambda.channelpointsminer.miner.api.ws;

import fr.rakambda.channelpointsminer.miner.api.pubsub.ITwitchPubSubWebSocketListener;
import fr.rakambda.channelpointsminer.miner.api.pubsub.TwitchPubSubWebSocketClient;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.ClaimAvailable;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.CommunityMomentStart;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.CreateNotification;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.DropClaim;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.DropProgress;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PointsEarned;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PointsSpent;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PredictionMade;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PredictionResult;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PredictionUpdated;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.RaidUpdateV2;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.ViewCount;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.claimavailable.ClaimAvailableData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.communitymoment.CommunityMomentStartData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.createnotification.CreateNotificationData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.createnotification.Notification;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.dropclaim.DropClaimData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.dropprogress.DropProgressData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.pointsearned.Balance;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.pointsearned.PointsEarnedData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.pointsspent.PointsSpentData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.predictionmade.PredictionMadeData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.predictionresult.PredictionResultData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.predictionupdated.PredictionUpdatedData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Claim;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.PointGain;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Prediction;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.PredictionResultPayload;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.PredictionResultType;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Raid;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.response.MessageData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.response.MessageResponse;
import fr.rakambda.channelpointsminer.miner.tests.TestUtils;
import fr.rakambda.channelpointsminer.miner.tests.WebsocketMockServer;
import fr.rakambda.channelpointsminer.miner.tests.WebsocketMockServerExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.ZonedDateTime;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.COMMUNITY_MOMENTS_CHANNEL_V1;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.COMMUNITY_POINTS_USER_V1;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.ONSITE_NOTIFICATIONS;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.PREDICTIONS_USER_V1;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.RAID;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.USER_DROP_EVENTS;
import static fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.TopicName.VIDEO_PLAYBACK_BY_ID;
import static java.time.ZoneOffset.UTC;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchPubSubWebSocketClientMessageTest{
	private static final int MESSAGE_TIMEOUT = 15000;
	
	private TwitchPubSubWebSocketClient tested;
	
	@Mock
	private ITwitchPubSubWebSocketListener listener;
	
	@AfterEach
	void tearDown(WebsocketMockServer server){
		tested.close();
		server.removeClients();
	}
	
	@Test
	void onPointsEarned(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/pointsEarned.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(COMMUNITY_POINTS_USER_V1)
								.target("123456789")
								.build())
						.message(PointsEarned.builder()
								.data(PointsEarnedData.builder()
										.timestamp(ZonedDateTime.of(2021, 10, 11, 16, 44, 35, 527485140, UTC))
										.channelId("987654321")
										.pointGain(PointGain.builder()
												.totalPoints(50)
												.reasonCode("CLAIM")
												.build())
										.balance(Balance.builder()
												.channelId("987654321")
												.balance(1500)
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onPointsSpent(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/pointsSpent.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(COMMUNITY_POINTS_USER_V1)
								.target("123456789")
								.build())
						.message(PointsSpent.builder()
								.data(PointsSpentData.builder()
										.timestamp(ZonedDateTime.of(2021, 10, 15, 19, 35, 30, 985882024, UTC))
										.balance(Balance.builder()
												.channelId("987654321")
												.balance(1500)
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onRaidUpdateV2(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/raidUpdateV2.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(RAID)
								.target("123456789")
								.build())
						.message(RaidUpdateV2.builder()
								.raid(Raid.builder()
										.id("raid-id")
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onPredictionMade(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/predictionMade.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(PREDICTIONS_USER_V1)
								.target("123456789")
								.build())
						.message(PredictionMade.builder()
								.data(PredictionMadeData.builder()
										.prediction(Prediction.builder()
												.eventId("event-id")
												.outcomeId("outcome-id")
												.channelId("987654321")
												.points(20)
												.predictedAt(ZonedDateTime.of(2021, 11, 4, 18, 34, 55, 594583209, UTC))
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onPredictionUpdated(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/predictionUpdated.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(PREDICTIONS_USER_V1)
								.target("123456789")
								.build())
						.message(PredictionUpdated.builder()
								.data(PredictionUpdatedData.builder()
										.prediction(Prediction.builder()
												.eventId("event-id")
												.outcomeId("outcome-id")
												.channelId("987654321")
												.points(200)
												.predictedAt(ZonedDateTime.of(2021, 11, 9, 21, 52, 36, 516144367, UTC))
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onPredictionResult(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/predictionResult.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(PREDICTIONS_USER_V1)
								.target("123456789")
								.build())
						.message(PredictionResult.builder()
								.data(PredictionResultData.builder()
										.timestamp(ZonedDateTime.of(2021, 11, 4, 18, 48, 18, 104721127, UTC))
										.prediction(Prediction.builder()
												.eventId("event-id")
												.outcomeId("outcome-id")
												.channelId("987654321")
												.points(1000)
												.predictedAt(ZonedDateTime.of(2021, 11, 4, 18, 45, 42, 619835769, UTC))
												.result(PredictionResultPayload.builder()
														.type(PredictionResultType.WIN)
														.pointsWon(1500)
														.build())
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onClaimAvailable(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/claimAvailable.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(COMMUNITY_POINTS_USER_V1)
								.target("123456789")
								.build())
						.message(ClaimAvailable.builder()
								.data(ClaimAvailableData.builder()
										.timestamp(ZonedDateTime.of(2021, 11, 15, 19, 0, 58, 685741905, UTC))
										.claim(Claim.builder()
												.id("claim-id")
												.channelId("987654321")
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onCreateNotification(WebsocketMockServer server) throws MalformedURLException{
		server.send(TestUtils.getAllResourceContent("api/ws/createNotification.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(ONSITE_NOTIFICATIONS)
								.target("123456789")
								.build())
						.message(CreateNotification.builder()
								.data(CreateNotificationData.builder()
										.notification(Notification.builder()
												.type("user_drop_reward_reminder_notification")
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onCreateNotification2(WebsocketMockServer server) throws MalformedURLException{
		server.send(TestUtils.getAllResourceContent("api/ws/createNotification2.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(ONSITE_NOTIFICATIONS)
								.target("123456789")
								.build())
						.message(CreateNotification.builder()
								.data(CreateNotificationData.builder()
										.notification(Notification.builder()
												.type("user_drop_reward_reminder_notification")
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onCommunityMomentStart(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/communityMomentStart.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(COMMUNITY_MOMENTS_CHANNEL_V1)
								.target("123456789")
								.build())
						.message(CommunityMomentStart.builder()
								.data(CommunityMomentStartData.builder()
										.momentId("moment-id")
										.channelId("123456789")
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onDropProgress(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/dropProgress.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(USER_DROP_EVENTS)
								.target("123456789")
								.build())
						.message(DropProgress.builder()
								.data(DropProgressData.builder()
										.channelId("987654321")
										.currentProgressMin(1)
										.requiredProgressMin(15)
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onDropClaimEvent(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/dropClaim.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(USER_DROP_EVENTS)
								.target("123456789")
								.build())
						.message(DropClaim.builder()
								.data(DropClaimData.builder()
										.channelId("987654321")
										.dropInstanceId("drop-instance-id")
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onViewCount(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/viewCount.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(VIDEO_PLAYBACK_BY_ID)
								.target("123456789")
								.build())
						.message(ViewCount.builder()
								.viewers(50L)
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onUnknownEvent(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/unknown.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(RAID)
								.target("123456789")
								.build())
						.build())
				.build();
		
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@BeforeEach
	void setUp(WebsocketMockServer server) throws InterruptedException{
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchPubSubWebSocketClient(uri);
		tested.setReuseAddr(true);
		tested.addListener(listener);
		tested.connectBlocking();
		server.awaitMessage();
		server.reset();
	}
}