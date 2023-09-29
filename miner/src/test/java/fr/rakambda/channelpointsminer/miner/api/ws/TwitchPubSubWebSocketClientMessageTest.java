package fr.rakambda.channelpointsminer.miner.api.ws;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.MultiplierReasonCode;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.ActiveMultipliersUpdated;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.ClaimAvailable;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.ClaimClaimed;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.Commercial;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.CommunityMomentStart;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.CreateNotification;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.DeleteNotification;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.DropClaim;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.DropProgress;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.PointsEarned;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.PointsSpent;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.PredictionMade;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.PredictionResult;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.PredictionUpdated;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.RaidCancelV2;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.RaidGoV2;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.RaidUpdateV2;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.ReadNotifications;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.UpdateSummary;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.ViewCount;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.WatchPartyVod;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.activemultipliersupdated.ActiveMultipliersUpdatedData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.claimavailable.ClaimAvailableData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.claimclaimed.ClaimClaimedData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.communitymoment.CommunityMomentStartData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification.CreateNotificationData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification.ImageBlock;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification.ImageContent;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification.ImageNotificationDataBlock;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification.Notification;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification.NotificationAction;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification.TextBlock;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification.TextContent;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification.TextNotificationDataBlock;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.deletenotification.DeleteNotificationData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.dropclaim.DropClaimData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.dropprogress.DropProgressData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.pointsearned.Balance;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.pointsspent.PointsSpentData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.predictionmade.PredictionMadeData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.predictionresult.PredictionResultData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.predictionupdated.PredictionUpdatedData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.readnotifications.ReadNotificationsData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.ActiveMultipliers;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Claim;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.CommunityPointsMultiplier;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.NotificationDisplayType;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.NotificationSummary;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.NotificationSummaryByDisplayType;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.PointGain;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Prediction;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.PredictionResultPayload;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.PredictionResultType;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Raid;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Summary;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.updatesummary.UpdateSummaryData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.watchpartyvod.Vod;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.api.ws.data.response.MessageData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.response.MessageResponse;
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
import java.net.URL;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import static fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.PointReasonCode.CLAIM;
import static fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.TopicName.COMMUNITY_MOMENTS_CHANNEL_V1;
import static fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.TopicName.COMMUNITY_POINTS_USER_V1;
import static fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.TopicName.ONSITE_NOTIFICATIONS;
import static fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.TopicName.PREDICTIONS_USER_V1;
import static fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.TopicName.RAID;
import static fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.TopicName.USER_DROP_EVENTS;
import static fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.TopicName.VIDEO_PLAYBACK_BY_ID;
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
	void onCommercial(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/commercial.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(VIDEO_PLAYBACK_BY_ID)
								.target("123456789")
								.build())
						.message(Commercial.builder()
								.serverTime(Instant.parse("2021-10-11T17:04:57.287128000Z"))
								.length(180)
								.scheduled(false)
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
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
												.userId("123456789")
												.channelId("987654321")
												.totalPoints(50)
												.baselinePoints(50)
												.reasonCode(CLAIM)
												.multipliers(List.of())
												.build())
										.balance(Balance.builder()
												.userId("123456789")
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
												.userId("123456789")
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
	void onRaidGoV2(WebsocketMockServer server) throws MalformedURLException{
		server.send(TestUtils.getAllResourceContent("api/ws/raidGoV2.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(RAID)
								.target("123456789")
								.build())
						.message(RaidGoV2.builder()
								.raid(Raid.builder()
										.id("raid-id")
										.creatorId("123456")
										.sourceId("456789")
										.targetId("987654")
										.targetLogin("target-name")
										.targetDisplayName("target-display-name")
										.targetProfileImage("https://google.com/target-image")
										.transitionJitterSeconds(0)
										.forceRaidNowSeconds(90)
										.viewerCount(200)
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onRaidUpdateV2(WebsocketMockServer server) throws MalformedURLException{
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
										.creatorId("123456")
										.sourceId("456789")
										.targetId("987654")
										.targetLogin("target-name")
										.targetDisplayName("target-display-name")
										.targetProfileImage("https://google.com/target-image")
										.transitionJitterSeconds(0)
										.forceRaidNowSeconds(90)
										.viewerCount(200)
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onRaidCancelV2(WebsocketMockServer server) throws MalformedURLException{
		server.send(TestUtils.getAllResourceContent("api/ws/raidCancelV2.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(RAID)
								.target("123456789")
								.build())
						.message(RaidCancelV2.builder()
								.raid(Raid.builder()
										.id("raid-id")
										.creatorId("123456")
										.sourceId("456789")
										.targetId("987654")
										.targetLogin("target-name")
										.targetDisplayName("target-display-name")
										.targetProfileImage("https://google.com/target-image")
										.transitionJitterSeconds(0)
										.forceRaidNowSeconds(90)
										.viewerCount(200)
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
								.serverTime(Instant.parse("2021-10-24T08:12:29.894667000Z"))
								.viewers(150)
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
										.timestamp(ZonedDateTime.of(2021, 11, 4, 18, 34, 55, 653758115, UTC))
										.prediction(Prediction.builder()
												.id("prediction-id")
												.eventId("event-id")
												.outcomeId("outcome-id")
												.channelId("987654321")
												.points(20)
												.predictedAt(ZonedDateTime.of(2021, 11, 4, 18, 34, 55, 594583209, UTC))
												.updatedAt(ZonedDateTime.of(2021, 11, 4, 18, 34, 55, 594583209, UTC))
												.userId("123456789")
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
										.timestamp(ZonedDateTime.of(2021, 11, 9, 21, 54, 2, 423996258, UTC))
										.prediction(Prediction.builder()
												.id("prediction-id")
												.eventId("event-id")
												.outcomeId("outcome-id")
												.channelId("987654321")
												.points(200)
												.predictedAt(ZonedDateTime.of(2021, 11, 9, 21, 52, 36, 516144367, UTC))
												.updatedAt(ZonedDateTime.of(2021, 11, 9, 21, 54, 2, 357265450, UTC))
												.userId("123456789")
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
												.id("prediction-id")
												.eventId("event-id")
												.outcomeId("outcome-id")
												.channelId("987654321")
												.points(1000)
												.predictedAt(ZonedDateTime.of(2021, 11, 4, 18, 45, 42, 619835769, UTC))
												.updatedAt(ZonedDateTime.of(2021, 11, 4, 18, 48, 18, 98606115, UTC))
												.userId("123456789")
												.result(PredictionResultPayload.builder()
														.type(PredictionResultType.WIN)
														.pointsWon(1500)
														.isAcknowledged(false)
														.build())
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onClaimClaimed(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/claimClaimed.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(COMMUNITY_POINTS_USER_V1)
								.target("123456789")
								.build())
						.message(ClaimClaimed.builder()
								.data(ClaimClaimedData.builder()
										.claim(Claim.builder()
												.id("claim-id")
												.userId("123456789")
												.channelId("987654321")
												.pointGain(PointGain.builder()
														.userId("123456789")
														.channelId("987654321")
														.totalPoints(60)
														.baselinePoints(50)
														.reasonCode(CLAIM)
														.multipliers(List.of(CommunityPointsMultiplier.builder()
																.reasonCode(MultiplierReasonCode.SUB_T1)
																.factor(0.2f)
																.build()))
														.build())
												.createdAt(ZonedDateTime.of(2021, 11, 24, 18, 37, 8, 0, UTC))
												.build())
										.timestamp(ZonedDateTime.of(2021, 11, 15, 19, 25, 9, 815949729, UTC))
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
												.userId("123456789")
												.channelId("987654321")
												.pointGain(PointGain.builder()
														.userId("123456789")
														.channelId("987654321")
														.totalPoints(50)
														.baselinePoints(50)
														.reasonCode(CLAIM)
														.build())
												.createdAt(ZonedDateTime.of(2021, 11, 15, 19, 0, 18, 0, UTC))
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
										.summary(NotificationSummary.builder()
												.unseenViewCount(5)
												.lastSeenAt(ZonedDateTime.of(2021, 1, 1, 19, 41, 56, 280058797, UTC))
												.viewerUnreadCount(8)
												.creatorUnreadCount(1)
												.build())
										.notification(Notification.builder()
												.userId("123456789")
												.id("notification-id")
												.body("notification-body")
												.bodyMd("notification-body-md")
												.type("user_drop_reward_reminder_notification")
												.renderStyle("DEFAULT")
												.thumbnailUrl(new URL("https://thumbnail.com"))
												.actions(List.of(NotificationAction.builder()
														.id("CTA")
														.type("click")
														.url(new URL("https://www.twitch.tv/inventory"))
														.modalId("")
														.body("Open")
														.label("CTA")
														.build()))
												.createdAt(ZonedDateTime.of(2022, 1, 15, 18, 45, 28, 412342347, UTC))
												.updatedAt(ZonedDateTime.of(2022, 1, 16, 18, 45, 28, 412342347, UTC))
												.read(false)
												.displayType(NotificationDisplayType.VIEWER)
												.category("transactional")
												.mobileDestinationType("ExternalLink")
												.mobileDestinationKey(new URL("https://www.twitch.tv/inventory"))
												.build())
										.persistent(true)
										.toast(false)
										.displayType(NotificationDisplayType.VIEWER)
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
												.userId("123456789")
												.id("notification-id")
												.body("notification-body")
												.bodyMd("notification-body-md")
												.type("user_drop_reward_reminder_notification")
												.renderStyle("DEFAULT")
												.thumbnailUrl(new URL("https://thumbnail.com"))
												.actions(List.of(NotificationAction.builder()
														.id("CTA")
														.type("click")
														.url(new URL("https://www.twitch.tv/inventory"))
														.modalId("")
														.body("Open")
														.label("CTA")
														.build()))
												.createdAt(ZonedDateTime.of(2022, 1, 15, 18, 45, 28, 412342347, UTC))
												.updatedAt(ZonedDateTime.of(2022, 1, 16, 18, 45, 28, 412342347, UTC))
												.read(false)
												.displayType(NotificationDisplayType.VIEWER)
												.mobileDestinationType("ExternalLink")
												.mobileDestinationKey(new URL("https://www.twitch.tv/inventory"))
												.dataBlocks(List.of(
														TextNotificationDataBlock.builder()
																.id("DEFAULT_BODY_TEXT")
																.content(TextContent.builder()
																		.textBlock(TextBlock.builder()
																				.body("text-block-body")
																				.build())
																		.build())
																.build(),
														ImageNotificationDataBlock.builder()
																.id("DEFAULT_THUMBNAIL_URL")
																.content(ImageContent.builder()
																		.imageBlock(ImageBlock.builder()
																				.url(new URL("https://data-block-thumbnail"))
																				.build())
																		.build())
																.build()
												))
												.build())
										.persistent(true)
										.toast(false)
										.displayType(NotificationDisplayType.VIEWER)
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onDeleteNotification(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/deleteNotification.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(ONSITE_NOTIFICATIONS)
								.target("123456789")
								.build())
						.message(DeleteNotification.builder()
								.data(DeleteNotificationData.builder()
										.notificationId("notification-id")
										.summary(NotificationSummary.builder()
												.unseenViewCount(5)
												.lastSeenAt(ZonedDateTime.of(2021, 1, 1, 19, 41, 56, 957079333, UTC))
												.viewerUnreadCount(8)
												.creatorUnreadCount(1)
												.build())
										.displayType(NotificationDisplayType.VIEWER)
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onUpdateSummary(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/updateSummary.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(ONSITE_NOTIFICATIONS)
								.target("123456789")
								.build())
						.message(UpdateSummary.builder()
								.data(UpdateSummaryData.builder()
										.summary(NotificationSummary.builder()
												.unseenViewCount(5)
												.lastSeenAt(ZonedDateTime.of(2021, 1, 1, 19, 41, 56, 957079333, UTC))
												.viewerUnreadCount(8)
												.creatorUnreadCount(1)
												.summariesByDisplayType(Map.of(
														NotificationDisplayType.CREATOR, NotificationSummaryByDisplayType.builder()
																.unreadSummary(Summary.builder()
																		.count(5)
																		.lastReadAll(ZonedDateTime.of(2021, 1, 1, 20, 41, 56, 957079333, UTC))
																		.build())
																.unseenSummary(Summary.builder()
																		.count(6)
																		.lastReadAll(ZonedDateTime.of(2021, 1, 1, 21, 41, 56, 957079333, UTC))
																		.build())
																.build(),
														NotificationDisplayType.VIEWER, NotificationSummaryByDisplayType.builder()
																.unreadSummary(Summary.builder()
																		.count(7)
																		.lastReadAll(ZonedDateTime.of(2021, 1, 1, 22, 41, 56, 957079333, UTC))
																		.build())
																.unseenSummary(Summary.builder()
																		.count(8)
																		.lastReadAll(ZonedDateTime.of(2021, 1, 1, 23, 41, 56, 957079333, UTC))
																		.build())
																.build()
												))
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onUpdateSummary2(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/updateSummary_2.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(ONSITE_NOTIFICATIONS)
								.target("123456789")
								.build())
						.message(UpdateSummary.builder()
								.data(UpdateSummaryData.builder()
										.summary(NotificationSummary.builder()
												.unseenViewCount(5)
												.lastSeenAt(ZonedDateTime.of(2021, 1, 1, 19, 41, 56, 957079333, UTC))
												.viewerUnreadCount(8)
												.creatorUnreadCount(1)
												.summariesByDisplayType(Map.of(
														NotificationDisplayType.CREATOR, NotificationSummaryByDisplayType.builder()
																.unreadSummary(Summary.builder()
																		.count(5)
																		.lastReadAll(ZonedDateTime.of(2021, 1, 1, 20, 41, 56, 957079333, UTC))
																		.build())
																.unseenSummary(Summary.builder()
																		.count(6)
																		.lastSeen(ZonedDateTime.of(2021, 1, 1, 21, 41, 56, 957079333, UTC))
																		.build())
																.build(),
														NotificationDisplayType.VIEWER, NotificationSummaryByDisplayType.builder()
																.unreadSummary(Summary.builder()
																		.count(7)
																		.lastReadAll(ZonedDateTime.of(2021, 1, 1, 22, 41, 56, 957079333, UTC))
																		.build())
																.unseenSummary(Summary.builder()
																		.count(8)
																		.lastSeen(ZonedDateTime.of(2021, 1, 1, 23, 41, 56, 957079333, UTC))
																		.build())
																.build()
												))
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onWatchPartyVod(WebsocketMockServer server) throws MalformedURLException{
		server.send(TestUtils.getAllResourceContent("api/ws/watchPartyVod.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(ONSITE_NOTIFICATIONS)
								.target("123456789")
								.build())
						.message(WatchPartyVod.builder()
								.vod(Vod.builder()
										.wpId("")
										.wpType("rerun")
										.incrementUrl(new URL("https://increment_url"))
										.vodId("123456")
										.title("the title")
										.broadcastType("highlight")
										.viewable("public")
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
										.clipSlug("clip-slug")
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onActiveMultipliersUpdated(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/activeMultipliersUpdated.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(COMMUNITY_POINTS_USER_V1)
								.target("123456789")
								.build())
						.message(ActiveMultipliersUpdated.builder()
								.data(ActiveMultipliersUpdatedData.builder()
										.timestamp(ZonedDateTime.of(2022, 8, 19, 21, 54, 20, 354269854, UTC))
										.activeMultipliers(ActiveMultipliers.builder()
												.userId("123456789")
												.channelId("987654321")
												.multipliers(List.of(CommunityPointsMultiplier.builder()
														.reasonCode(MultiplierReasonCode.SUB_T1)
														.factor(0.2F)
														.build()))
												.build())
										.build())
								.build())
						.build())
				.build();
		verify(listener, timeout(MESSAGE_TIMEOUT)).onWebSocketMessage(expected);
	}
	
	@Test
	void onReadNotifications(WebsocketMockServer server){
		server.send(TestUtils.getAllResourceContent("api/ws/readNotifications.json"));
		
		var expected = MessageResponse.builder()
				.data(MessageData.builder()
						.topic(Topic.builder()
								.name(ONSITE_NOTIFICATIONS)
								.target("123456789")
								.build())
						.message(ReadNotifications.builder()
								.data(ReadNotificationsData.builder()
										.notificationIds(List.of("notification-id"))
										.displayType("VIEWER")
										.summary(NotificationSummary.builder()
												.unseenViewCount(5)
												.lastSeenAt(ZonedDateTime.of(2021, 1, 1, 19, 41, 56, 957079333, UTC))
												.viewerUnreadCount(8)
												.creatorUnreadCount(1)
												.summariesByDisplayType(Map.of(
														NotificationDisplayType.CREATOR, NotificationSummaryByDisplayType.builder()
																.unreadSummary(Summary.builder()
																		.count(5)
																		.lastReadAll(ZonedDateTime.of(2021, 1, 1, 20, 41, 56, 957079333, UTC))
																		.build())
																.unseenSummary(Summary.builder()
																		.count(6)
																		.lastSeen(ZonedDateTime.of(2021, 1, 1, 21, 41, 56, 957079333, UTC))
																		.build())
																.build(),
														NotificationDisplayType.VIEWER, NotificationSummaryByDisplayType.builder()
																.unreadSummary(Summary.builder()
																		.count(7)
																		.lastReadAll(ZonedDateTime.of(2021, 1, 1, 22, 41, 56, 957079333, UTC))
																		.build())
																.unseenSummary(Summary.builder()
																		.count(8)
																		.lastSeen(ZonedDateTime.of(2021, 1, 1, 23, 41, 56, 957079333, UTC))
																		.build())
																.build()
												))
												.build())
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
										.dropId("drop-id")
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
										.dropId("drop-id")
										.channelId("987654321")
										.dropInstanceId("drop-instance-id")
										.build())
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