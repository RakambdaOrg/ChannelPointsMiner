package fr.raksrinana.twitchminer.api.ws;

import fr.raksrinana.twitchminer.api.ws.data.message.*;
import fr.raksrinana.twitchminer.api.ws.data.message.pointsearned.Balance;
import fr.raksrinana.twitchminer.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.raksrinana.twitchminer.api.ws.data.message.pointsspent.PointsSpentData;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.PointGain;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Raid;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.api.ws.data.response.MessageData;
import fr.raksrinana.twitchminer.api.ws.data.response.MessageResponse;
import fr.raksrinana.twitchminer.tests.WebsocketMockServer;
import fr.raksrinana.twitchminer.tests.WebsocketMockServerExtension;
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
import static fr.raksrinana.twitchminer.api.ws.data.message.subtype.PointReasonCode.CLAIM;
import static fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName.*;
import static fr.raksrinana.twitchminer.tests.TestUtils.getAllResourceContent;
import static java.time.ZoneOffset.UTC;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(WebsocketMockServerExtension.class)
class TwitchWebSocketClientMessageTest{
	private static final int MESSAGE_TIMEOUT = 15000;
	
	private TwitchWebSocketClient tested;
	
	@Mock
	private TwitchWebSocketListener listener;
	
	@BeforeEach
	void setUp(){
		var uri = URI.create("ws://127.0.0.1:" + WebsocketMockServerExtension.PORT);
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
	void onCommercial(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
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
	void onPointsEarned(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
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
	void onPointsSpent(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
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
	void onRaidGoV2(WebsocketMockServer server) throws InterruptedException, MalformedURLException{
		tested.connectBlocking();
		
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
	void onRaidUpdateV2(WebsocketMockServer server) throws InterruptedException, MalformedURLException{
		tested.connectBlocking();
		
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
	void onViewCount(WebsocketMockServer server) throws InterruptedException{
		tested.connectBlocking();
		
		server.send(getAllResourceContent("api/ws/viewcount.json"));
		
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
}