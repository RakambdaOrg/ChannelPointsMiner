package fr.raksrinana.channelpointsminer.log;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.api.discord.data.Webhook;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.Balance;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsspent.PointsSpentData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionresult.PredictionResultData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.*;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.handler.data.PlacedPrediction;
import fr.raksrinana.channelpointsminer.log.event.*;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscordLogEventListenerMessageTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-name";
	private static final String USERNAME = "username";
	private static final String UNKNOWN_STREAMER = "UnknownStreamer";
	
	private DiscordLogEventListener tested;
	
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
		tested = new DiscordLogEventListener(discordApi, false);
		
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		lenient().when(miner.getUsername()).thenReturn(USERNAME);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
		
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void onClaimAvailable(){
		tested.onLogEvent(new ClaimAvailableLogEvent(miner, streamer));
		
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
		when(pointGain.getTotalPoints()).thenReturn(25);
		when(pointGain.getReasonCode()).thenReturn(PointReasonCode.CLAIM);
		when(balance.getBalance()).thenReturn(200);
		
		tested.onLogEvent(new PointsEarnedLogEvent(miner, streamer, data));
		
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
		when(pointGain.getTotalPoints()).thenReturn(2500);
		when(pointGain.getReasonCode()).thenReturn(PointReasonCode.CLAIM);
		when(balance.getBalance()).thenReturn(12345678);
		
		tested.onLogEvent(new PointsEarnedLogEvent(miner, streamer, data));
		
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
		when(pointGain.getTotalPoints()).thenReturn(-2500);
		when(pointGain.getReasonCode()).thenReturn(PointReasonCode.CLAIM);
		when(balance.getBalance()).thenReturn(12345678);
		
		tested.onLogEvent(new PointsEarnedLogEvent(miner, streamer, data));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 💰 %s : Points earned [%s | %s | %s]".formatted(USERNAME, STREAMER_USERNAME, "-2.5K", "CLAIM", "12.35M"))
				.build());
	}
	
	@Test
	void onPointsSpent(){
		var data = mock(PointsSpentData.class);
		var balance = mock(Balance.class);
		
		when(data.getBalance()).thenReturn(balance);
		when(balance.getBalance()).thenReturn(25);
		
		tested.onLogEvent(new PointsSpentLogEvent(miner, streamer, data));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 💸 %s : Points spent [%d]".formatted(USERNAME, STREAMER_USERNAME, 25))
				.build());
	}
	
	@Test
	void onStreamUp(){
		tested.onLogEvent(new StreamUpLogEvent(miner, streamer));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ▶️ %s : Stream started".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void authorNotFound(){
		tested.onLogEvent(new StreamUpLogEvent(miner, null));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ▶️ %s : Stream started".formatted(USERNAME, UNKNOWN_STREAMER))
				.build());
	}
	
	@Test
	void onStreamDown(){
		tested.onLogEvent(new StreamDownLogEvent(miner, streamer));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ⏹️ %s : Stream stopped".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onEventCreated(){
		var title = "MyTitle";
		var event = mock(Event.class);
		
		when(event.getTitle()).thenReturn(title);
		
		tested.onLogEvent(new EventCreatedLogEvent(miner, streamer, event));
		
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
		when(prediction.getEvent()).thenReturn(event);
		when(event.getOutcomes()).thenReturn(List.of(outcome1, outcome2));
		when(outcome1.getId()).thenReturn("bad-id");
		when(outcome2.getId()).thenReturn(outcomeId);
		when(outcome2.getColor()).thenReturn(OutcomeColor.BLUE);
		when(outcome2.getTitle()).thenReturn(outcomeName);
		
		tested.onLogEvent(new PredictionMadeLogEvent(miner, streamer, placedPrediction));
		
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
		when(prediction.getEvent()).thenReturn(event);
		when(event.getOutcomes()).thenReturn(List.of(outcome1));
		when(outcome1.getId()).thenReturn("bad-id");
		
		tested.onLogEvent(new PredictionMadeLogEvent(miner, streamer, placedPrediction));
		
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
		when(prediction.getResult()).thenReturn(result);
		when(result.getType()).thenReturn(PredictionResultType.WIN);
		when(result.getPointsWon()).thenReturn(56);
		
		tested.onLogEvent(new PredictionResultLogEvent(miner, streamer, placedPrediction, predictionResultData));
		
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
		when(prediction.getResult()).thenReturn(result);
		when(result.getType()).thenReturn(PredictionResultType.REFUND);
		
		tested.onLogEvent(new PredictionResultLogEvent(miner, streamer, placedPrediction, predictionResultData));
		
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
		when(prediction.getResult()).thenReturn(result);
		when(result.getType()).thenReturn(PredictionResultType.WIN);
		when(result.getPointsWon()).thenReturn(56);
		
		tested.onLogEvent(new PredictionResultLogEvent(miner, streamer, null, predictionResultData));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] 🧧 %s : Bet result [%s | Unknown final gain, obtained %+d points]".formatted(USERNAME, STREAMER_USERNAME, "WIN", 56))
				.build());
	}
	
	@Test
	void onMinerStarted(){
		var version = "test-version";
		var commit = "test-commit";
		var branch = "test-branch";
		tested.onLogEvent(new MinerStartedLogEvent(miner, version, commit, branch));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ✅ %s : Miner started (version: %s [%s - %s])".formatted(USERNAME, UNKNOWN_STREAMER, version, commit, branch))
				.build());
	}
	
	@Test
	void onStreamerAdded(){
		tested.onLogEvent(new StreamerAddedLogEvent(miner, streamer));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ➕ %s : Streamer added".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onStreamerRemoved(){
		tested.onLogEvent(new StreamerRemovedLogEvent(miner, streamer));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("[%s] ➖ %s : Streamer removed".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
}