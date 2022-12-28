package fr.rakambda.channelpointsminer.miner.log.discord;

import fr.rakambda.channelpointsminer.miner.api.discord.data.Webhook;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDrop;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.pointsearned.Balance;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.pointsspent.PointsSpentData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.predictionresult.PredictionResultData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.OutcomeColor;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.PointGain;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.PointReasonCode;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Prediction;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.PredictionResultPayload;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.PredictionResultType;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.config.DiscordEventConfiguration;
import fr.rakambda.channelpointsminer.miner.event.impl.ClaimAvailableEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.ClaimMomentEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.ClaimedMomentEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.DropClaimEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.DropClaimedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.EventCreatedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.MinerStartedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsEarnedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsSpentEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionMadeEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionResultEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamDownEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamUpEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerAddedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerRemovedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerUnknownEvent;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.handler.data.PlacedPrediction;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class DiscordMessageBuilderMessageTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-name";
	private static final String USERNAME = "username";
	private static final String UNKNOWN_STREAMER = "UnknownStreamer";
	private static final Instant NOW = Instant.parse("2020-05-17T12:14:20.000Z");
	private static final ZonedDateTime ZONED_NOW = ZonedDateTime.ofInstant(NOW, ZoneId.systemDefault());
	
	@InjectMocks
	private DiscordMessageBuilder tested;
	
	@Mock
	private DiscordEventConfiguration discordEventConfiguration;
	@Mock
	private IMiner miner;
	@Mock
	private Streamer streamer;
	@Mock
	private Topic topic;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		lenient().when(miner.getUsername()).thenReturn(USERNAME);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
		
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void onClaimAvailableWithCustomFormat(){
		when(discordEventConfiguration.getFormat()).thenReturn("{streamer} override test");
		
		var webhook = tested.createSimpleMessage(new ClaimAvailableEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("%s override test".formatted(STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onClaimAvailable(){
		var webhook = tested.createSimpleMessage(new ClaimAvailableEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] 🎫 %s : Claim available".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onClaimMoment(){
		var webhook = tested.createSimpleMessage(new ClaimMomentEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] 🎖️ %s : Moment available".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onClaimedMoment(){
		var webhook = tested.createSimpleMessage(new ClaimedMomentEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] 🎖️ %s : Moment claimed".formatted(USERNAME, STREAMER_USERNAME))
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
		
		var webhook = tested.createSimpleMessage(new PointsEarnedEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
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
		
		var webhook = tested.createSimpleMessage(new PointsEarnedEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
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
		
		var webhook = tested.createSimpleMessage(new PointsEarnedEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
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
		
		var webhook = tested.createSimpleMessage(new PointsSpentEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] 💸 %s : Points spent [%d]".formatted(USERNAME, STREAMER_USERNAME, 25))
				.build());
	}
	
	@Test
	void onStreamUp(){
		var webhook = tested.createSimpleMessage(new StreamUpEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] ▶️ %s : Stream started".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void authorNotFound(){
		var webhook = tested.createSimpleMessage(new StreamUpEvent(miner, STREAMER_ID, null, null, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] ▶️ %s : Stream started".formatted(USERNAME, UNKNOWN_STREAMER))
				.build());
	}
	
	@Test
	void onStreamDown(){
		var webhook = tested.createSimpleMessage(new StreamDownEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] ⏹️ %s : Stream stopped".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onEventCreated(){
		var title = "MyTitle";
		var event = mock(Event.class);
		
		when(event.getTitle()).thenReturn(title);
		when(event.getCreatedAt()).thenReturn(ZONED_NOW);
		
		var webhook = tested.createSimpleMessage(new EventCreatedEvent(miner, streamer, event), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
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
		
		var webhook = tested.createSimpleMessage(new PredictionMadeEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
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
		
		var webhook = tested.createSimpleMessage(new PredictionMadeEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
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
		
		var webhook = tested.createSimpleMessage(new PredictionResultEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction, predictionResultData), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
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
		
		var webhook = tested.createSimpleMessage(new PredictionResultEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction, predictionResultData), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
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
		
		var webhook = tested.createSimpleMessage(new PredictionResultEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, null, predictionResultData), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] 🧧 %s : Bet result [%s | Unknown final gain, obtained %+d points]".formatted(USERNAME, STREAMER_USERNAME, "WIN", 56))
				.build());
	}
	
	@Test
	void onMinerStarted(){
		var version = "test-version";
		var commit = "test-commit";
		var branch = "test-branch";
		var webhook = tested.createSimpleMessage(new MinerStartedEvent(miner, version, commit, branch, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] ✅ : Miner started with version %s [%s - %s]".formatted(USERNAME, version, commit, branch))
				.build());
	}
	
	@Test
	void onStreamerAdded(){
		var webhook = tested.createSimpleMessage(new StreamerAddedEvent(miner, streamer, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] ➕ %s : Streamer added".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onStreamerRemoved(){
		var webhook = tested.createSimpleMessage(new StreamerRemovedEvent(miner, streamer, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] ➖ %s : Streamer removed".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onStreamerUnknown(){
		var webhook = tested.createSimpleMessage(new StreamerUnknownEvent(miner, STREAMER_USERNAME, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] ❌ %s : Streamer unknown".formatted(USERNAME, STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onDropClaim(){
		var name = "drop-name";
		var drop = mock(TimeBasedDrop.class);
		when(drop.getName()).thenReturn(name);
		
		var webhook = tested.createSimpleMessage(new DropClaimEvent(miner, drop, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] 🎁 : Drop available [%s]".formatted(USERNAME, name))
				.build());
	}
	
	@Test
	void onDropClaimed(){
		var name = "drop-name";
		var drop = mock(TimeBasedDrop.class);
		when(drop.getName()).thenReturn(name);
		
		var webhook = tested.createSimpleMessage(new DropClaimedEvent(miner, drop, NOW), discordEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.content("[%s] 🎁 : Drop claimed [%s]".formatted(USERNAME, name))
				.build());
	}
}