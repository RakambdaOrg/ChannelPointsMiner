package fr.raksrinana.channelpointsminer.api.ws;

import fr.raksrinana.channelpointsminer.api.gql.data.types.MultiplierReasonCode;
import fr.raksrinana.channelpointsminer.api.ws.data.message.*;
import fr.raksrinana.channelpointsminer.api.ws.data.message.claimclaimed.ClaimClaimedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.Balance;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsspent.PointsSpentData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionmade.PredictionMadeData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionresult.PredictionResultData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionupdated.PredictionUpdatedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.*;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.api.ws.data.response.MessageData;
import fr.raksrinana.channelpointsminer.api.ws.data.response.MessageResponse;
import fr.raksrinana.channelpointsminer.tests.WebsocketMockServer;
import fr.raksrinana.channelpointsminer.tests.WebsocketMockServerExtension;
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
import static fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PointReasonCode.CLAIM;
import static fr.raksrinana.channelpointsminer.api.ws.data.request.topic.TopicName.*;
import static fr.raksrinana.channelpointsminer.tests.TestUtils.getAllResourceContent;
import static java.time.ZoneOffset.UTC;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchWebSocketClientMessageTest{
	private static final int MESSAGE_TIMEOUT = 15000;
	
	private TwitchWebSocketClient tested;
	
	@Mock
	private ITwitchWebSocketListener listener;
	
	@BeforeEach
	void setUp(WebsocketMockServer server) throws InterruptedException{
		var uri = URI.create("ws://127.0.0.1:" + server.getPort());
		tested = new TwitchWebSocketClient(uri);
		tested.setReuseAddr(true);
		tested.addListener(listener);
		tested.connectBlocking();
		server.awaitMessage();
		server.reset();
	}
	
	@AfterEach
	void tearDown(WebsocketMockServer server){
		tested.close();
		server.removeClients();
	}
	
	@Test
	void onCommercial(WebsocketMockServer server){
		server.send(getAllResourceContent("api/ws/commercial.json"));
		
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
		server.send(getAllResourceContent("api/ws/pointsEarned.json"));
		
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
		server.send(getAllResourceContent("api/ws/pointsSpent.json"));
		
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
		server.send(getAllResourceContent("api/ws/raidGoV2.json"));
		
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
										.targetProfileImage(new URL("https://google.com/target-image"))
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
		server.send(getAllResourceContent("api/ws/raidUpdateV2.json"));
		
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
										.targetProfileImage(new URL("https://google.com/target-image"))
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
		server.send(getAllResourceContent("api/ws/viewCount.json"));
		
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
		server.send(getAllResourceContent("api/ws/predictionMade.json"));
		
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
		server.send(getAllResourceContent("api/ws/predictionUpdated.json"));
		
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
		server.send(getAllResourceContent("api/ws/predictionResult.json"));
		
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
		server.send(getAllResourceContent("api/ws/claimClaimed.json"));
		
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
}