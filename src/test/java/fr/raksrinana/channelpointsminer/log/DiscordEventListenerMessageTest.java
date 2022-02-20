package fr.raksrinana.channelpointsminer.log;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.api.discord.data.Webhook;
import fr.raksrinana.channelpointsminer.api.gql.data.types.TimeBasedDrop;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.Balance;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsspent.PointsSpentData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionresult.PredictionResultData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.OutcomeColor;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PointGain;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PointReasonCode;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Prediction;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PredictionResultPayload;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PredictionResultType;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.event.IEvent;
import fr.raksrinana.channelpointsminer.event.impl.ClaimAvailableEvent;
import fr.raksrinana.channelpointsminer.event.impl.DropClaimEvent;
import fr.raksrinana.channelpointsminer.event.impl.EventCreatedEvent;
import fr.raksrinana.channelpointsminer.event.impl.MinerStartedEvent;
import fr.raksrinana.channelpointsminer.event.impl.PointsEarnedEvent;
import fr.raksrinana.channelpointsminer.event.impl.PointsSpentEvent;
import fr.raksrinana.channelpointsminer.event.impl.PredictionMadeEvent;
import fr.raksrinana.channelpointsminer.event.impl.PredictionResultEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamDownEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamUpEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamerAddedEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamerRemovedEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamerUnknownEvent;
import fr.raksrinana.channelpointsminer.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.handler.data.PlacedPrediction;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscordEventListenerMessageTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-name";
	private static final String USERNAME = "username";
	private static final String UNKNOWN_STREAMER = "UnknownStreamer";
	private static final Instant NOW = Instant.parse("2020-05-17T12:14:20.000Z");
	private static final ZonedDateTime ZONED_NOW = ZonedDateTime.ofInstant(NOW, ZoneId.systemDefault());
	
	private DiscordEventListener tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private DiscordApi discordApi;
	@Mock
	private Streamer streamer;
	@Mock
	private Topic topic;
	
	@BeforeEach
	void setUp(){
		tested = new DiscordEventListener(discordApi, false);
		
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		lenient().when(miner.getUsername()).thenReturn(USERNAME);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
		
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void notLoggableEventIsIgnored(){
		var event = mock(IEvent.class);
		
		tested.onEvent(event);
		
		verify(discordApi, never()).sendMessage(any());
	}
	
	@Test
	void onClaimAvailable(){
		tested.onEvent(new ClaimAvailableEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 🎫 %s : Claim available".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onPointsEarned(){
		var data = mock(PointsEarnedData.class);
		var pointGain = mock(PointGain.class);
		var balance = mock(Balance.class);
		
		when(data.getPointGain()).thenReturn(pointGain);
		when(data.getBalance()).thenReturn(balance);
		when(data.getTimestamp()).thenReturn(ZONED_NOW);
		when(pointGain.getTotalPoints()).thenReturn(25);
		when(pointGain.getReasonCode()).thenReturn(PointReasonCode.CLAIM);
		when(balance.getBalance()).thenReturn(200);
		
		tested.onEvent(new PointsEarnedEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 💰 %s : Points earned [%+d | %s | %d]".formatted(USERNAME, STREAMER_USERNAME, 25, "CLAIM", 200))
				.build());
	}
	
	@Test
	void onPointsEarnedBigValue(){
		var data = mock(PointsEarnedData.class);
		var pointGain = mock(PointGain.class);
		var balance = mock(Balance.class);
		
		when(data.getPointGain()).thenReturn(pointGain);
		when(data.getBalance()).thenReturn(balance);
		when(data.getTimestamp()).thenReturn(ZONED_NOW);
		when(pointGain.getTotalPoints()).thenReturn(2500);
		when(pointGain.getReasonCode()).thenReturn(PointReasonCode.CLAIM);
		when(balance.getBalance()).thenReturn(12345678);
		
		tested.onEvent(new PointsEarnedEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 💰 %s : Points earned [%s | %s | %s]".formatted(USERNAME, STREAMER_USERNAME, "+2.5K", "CLAIM", "12.35M"))
				.build());
	}
	
	@Test
	void onPointsEarnedBigNegativeValue(){
		var data = mock(PointsEarnedData.class);
		var pointGain = mock(PointGain.class);
		var balance = mock(Balance.class);
		
		when(data.getPointGain()).thenReturn(pointGain);
		when(data.getBalance()).thenReturn(balance);
		when(data.getTimestamp()).thenReturn(ZONED_NOW);
		when(pointGain.getTotalPoints()).thenReturn(-2500);
		when(pointGain.getReasonCode()).thenReturn(PointReasonCode.CLAIM);
		when(balance.getBalance()).thenReturn(12345678);
		
		tested.onEvent(new PointsEarnedEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 💰 %s : Points earned [%s | %s | %s]".formatted(USERNAME, STREAMER_USERNAME, "-2.5K", "CLAIM", "12.35M"))
				.build());
	}
	
	@Test
	void onPointsSpent(){
		var data = mock(PointsSpentData.class);
		var balance = mock(Balance.class);
		
		when(data.getBalance()).thenReturn(balance);
		when(data.getTimestamp()).thenReturn(ZONED_NOW);
		when(balance.getBalance()).thenReturn(25);
		
		tested.onEvent(new PointsSpentEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 💸 %s : Points spent [%d]".formatted(USERNAME, STREAMER_USERNAME, 25))
				.build());
	}
	
	@Test
	void onStreamUp(){
		tested.onEvent(new StreamUpEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ▶️ %s : Stream started".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void authorNotFound(){
		tested.onEvent(new StreamUpEvent(miner, STREAMER_ID, null, null, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ▶️ %s : Stream started".formatted(USERNAME, UNKNOWN_STREAMER))
				.build());
	}
	
	@Test
	void onStreamDown(){
		tested.onEvent(new StreamDownEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ⏹️ %s : Stream stopped".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onEventCreated(){
		var title = "MyTitle";
		var event = mock(Event.class);
		
		when(event.getTitle()).thenReturn(title);
		when(event.getCreatedAt()).thenReturn(ZONED_NOW);
		
		tested.onEvent(new EventCreatedEvent(miner, streamer, event));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 📑 %s : Prediction created [%s]".formatted(USERNAME, STREAMER_USERNAME, title))
				.build());
	}
	
	@Test
	void onPredictionMade(){
		var outcomeId = "outcome-id";
		var outcomeName = "Out2";
		var placedPrediction = mock(PlacedPrediction.class);
		var prediction = mock(BettingPrediction.class);
		var event = mock(Event.class);
		var outcome1 = mock(Outcome.class);
		var outcome2 = mock(Outcome.class);
		
		when(placedPrediction.getAmount()).thenReturn(25);
		when(placedPrediction.getOutcomeId()).thenReturn(outcomeId);
		when(placedPrediction.getBettingPrediction()).thenReturn(prediction);
		when(placedPrediction.getPredictedAt()).thenReturn(NOW);
		when(prediction.getEvent()).thenReturn(event);
		when(event.getOutcomes()).thenReturn(List.of(outcome1, outcome2));
		when(outcome1.getId()).thenReturn("bad-id");
		when(outcome2.getId()).thenReturn(outcomeId);
		when(outcome2.getColor()).thenReturn(OutcomeColor.BLUE);
		when(outcome2.getTitle()).thenReturn(outcomeName);
		
		tested.onEvent(new PredictionMadeEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 🪙 %s : Bet placed [%d | %s: %s]".formatted(USERNAME, STREAMER_USERNAME, 25, "BLUE", outcomeName))
				.build());
	}
	
	@Test
	void onPredictionMadeUnknownOutcome(){
		var placedPrediction = mock(PlacedPrediction.class);
		var prediction = mock(BettingPrediction.class);
		var event = mock(Event.class);
		var outcome1 = mock(Outcome.class);
		
		when(placedPrediction.getAmount()).thenReturn(25);
		when(placedPrediction.getOutcomeId()).thenReturn("outcome-id");
		when(placedPrediction.getBettingPrediction()).thenReturn(prediction);
		when(placedPrediction.getPredictedAt()).thenReturn(NOW);
		when(prediction.getEvent()).thenReturn(event);
		when(event.getOutcomes()).thenReturn(List.of(outcome1));
		when(outcome1.getId()).thenReturn("bad-id");
		
		tested.onEvent(new PredictionMadeEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 🪙 %s : Bet placed [%d | %s]".formatted(USERNAME, STREAMER_USERNAME, 25, "UnknownOutcome"))
				.build());
	}
	
	@Test
	void onPredictionResult(){
		var placedPrediction = mock(PlacedPrediction.class);
		var predictionResultData = mock(PredictionResultData.class);
		var prediction = mock(Prediction.class);
		var result = mock(PredictionResultPayload.class);
		
		when(placedPrediction.getAmount()).thenReturn(16);
		when(predictionResultData.getPrediction()).thenReturn(prediction);
		when(predictionResultData.getTimestamp()).thenReturn(ZONED_NOW);
		when(prediction.getResult()).thenReturn(result);
		when(result.getType()).thenReturn(PredictionResultType.WIN);
		when(result.getPointsWon()).thenReturn(56);
		
		tested.onEvent(new PredictionResultEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction, predictionResultData));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 🧧 %s : Bet result [%s | +%d]".formatted(USERNAME, STREAMER_USERNAME, "WIN", 40))
				.build());
	}
	
	@Test
	void onPredictionResultRefund(){
		var placedPrediction = mock(PlacedPrediction.class);
		var predictionResultData = mock(PredictionResultData.class);
		var prediction = mock(Prediction.class);
		var result = mock(PredictionResultPayload.class);
		
		when(predictionResultData.getPrediction()).thenReturn(prediction);
		when(predictionResultData.getTimestamp()).thenReturn(ZONED_NOW);
		when(prediction.getResult()).thenReturn(result);
		when(result.getType()).thenReturn(PredictionResultType.REFUND);
		
		tested.onEvent(new PredictionResultEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction, predictionResultData));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 🧧 %s : Bet result [%s | %d]".formatted(USERNAME, STREAMER_USERNAME, "REFUND", 0))
				.build());
	}
	
	@Test
	void onPredictionResultNoPlacedPrediction(){
		var predictionResultData = mock(PredictionResultData.class);
		var prediction = mock(Prediction.class);
		var result = mock(PredictionResultPayload.class);
		
		when(predictionResultData.getPrediction()).thenReturn(prediction);
		when(predictionResultData.getTimestamp()).thenReturn(ZONED_NOW);
		when(prediction.getResult()).thenReturn(result);
		when(result.getType()).thenReturn(PredictionResultType.WIN);
		when(result.getPointsWon()).thenReturn(56);
		
		tested.onEvent(new PredictionResultEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, null, predictionResultData));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 🧧 %s : Bet result [%s | Unknown final gain, obtained %+d points]".formatted(USERNAME, STREAMER_USERNAME, "WIN", 56))
				.build());
	}
	
	@Test
	void onMinerStarted(){
		var version = "test-version";
		var commit = "test-commit";
		var branch = "test-branch";
		tested.onEvent(new MinerStartedEvent(miner, version, commit, branch, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ✅ : Miner started (version: %s [%s - %s])".formatted(USERNAME, version, commit, branch))
				.build());
	}
	
	@Test
	void onStreamerAdded(){
		tested.onEvent(new StreamerAddedEvent(miner, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ➕ %s : Streamer added".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onStreamerRemoved(){
		tested.onEvent(new StreamerRemovedEvent(miner, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ➖ %s : Streamer removed".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onStreamerUnknown(){
		tested.onEvent(new StreamerUnknownEvent(miner, STREAMER_USERNAME, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ❌ %s : Streamer unknown".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onDropClaim(){
		var name = "drop-name";
		var drop = mock(TimeBasedDrop.class);
		when(drop.getName()).thenReturn(name);
		
		tested.onEvent(new DropClaimEvent(miner, drop, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 🎁 : Claiming drop [%s]".formatted(USERNAME, name))
				.build());
	}
}