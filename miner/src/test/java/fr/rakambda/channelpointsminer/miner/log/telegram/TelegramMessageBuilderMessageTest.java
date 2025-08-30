package fr.rakambda.channelpointsminer.miner.log.telegram;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDrop;
import fr.rakambda.channelpointsminer.miner.api.telegram.data.Message;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.pointsearned.Balance;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.pointsearned.PointsEarnedData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.pointsspent.PointsSpentData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.predictionresult.PredictionResultData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.OutcomeColor;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.PointGain;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Prediction;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.PredictionResultPayload;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.PredictionResultType;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.config.MessageEventConfiguration;
import fr.rakambda.channelpointsminer.miner.event.impl.ClaimAvailableEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.ClaimMomentEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.ClaimedMomentEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.DropClaimEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.DropClaimedChannelEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.DropClaimedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.DropProgressChannelEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.EventCreatedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.LoginRequiredEvent;
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
class TelegramMessageBuilderMessageTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-name";
	private static final String USERNAME = "username";
	private static final String TARGET_USERNAME = "target-username";
	private static final String UNKNOWN_STREAMER = "UnknownStreamer";
	private static final Instant NOW = Instant.parse("2020-05-17T12:14:20.000Z");
	private static final ZonedDateTime ZONED_NOW = ZonedDateTime.ofInstant(NOW, ZoneId.systemDefault());
	
	@InjectMocks
	private TelegramMessageBuilder tested;
	
	@Mock
	private MessageEventConfiguration messageEventConfiguration;
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
		when(messageEventConfiguration.getFormat()).thenReturn("{streamer} override test");
		
		var event = new ClaimAvailableEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("%s override test".formatted(STREAMER_USERNAME))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onClaimAvailable(){
		var event = new ClaimAvailableEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🎫 %s : Claim available".formatted(USERNAME, STREAMER_USERNAME))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onClaimMoment(){
		var event = new ClaimMomentEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🎖️ %s : Moment available".formatted(USERNAME, STREAMER_USERNAME))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onClaimedMoment(){
		var event = new ClaimedMomentEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🎖️ %s : Moment claimed".formatted(USERNAME, STREAMER_USERNAME))
				.chatId(TARGET_USERNAME)
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
		when(pointGain.getReasonCode()).thenReturn("CLAIM");
		when(balance.getBalance()).thenReturn(200);
		
		var event = new PointsEarnedEvent(STREAMER_ID, STREAMER_USERNAME, streamer, data);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 💰 %s : Points earned [%+d | %s | %d]".formatted(USERNAME, STREAMER_USERNAME, 25, "CLAIM", 200))
				.chatId(TARGET_USERNAME)
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
		when(pointGain.getReasonCode()).thenReturn("CLAIM");
		when(balance.getBalance()).thenReturn(12345678);
		
		var event = new PointsEarnedEvent(STREAMER_ID, STREAMER_USERNAME, streamer, data);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 💰 %s : Points earned [%s | %s | %s]".formatted(USERNAME, STREAMER_USERNAME, "+2.5K", "CLAIM", "12.35M"))
				.chatId(TARGET_USERNAME)
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
		when(pointGain.getReasonCode()).thenReturn("CLAIM");
		when(balance.getBalance()).thenReturn(12345678);
		
		var event = new PointsEarnedEvent(STREAMER_ID, STREAMER_USERNAME, streamer, data);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 💰 %s : Points earned [%s | %s | %s]".formatted(USERNAME, STREAMER_USERNAME, "-2.5K", "CLAIM", "12.35M"))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onPointsSpent(){
		var data = mock(PointsSpentData.class);
		var balance = mock(Balance.class);
		
		when(data.getBalance()).thenReturn(balance);
		when(data.getTimestamp()).thenReturn(ZONED_NOW);
		when(balance.getBalance()).thenReturn(25);
		
		var event = new PointsSpentEvent(STREAMER_ID, STREAMER_USERNAME, streamer, data);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 💸 %s : Points spent [%d]".formatted(USERNAME, STREAMER_USERNAME, 25))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onStreamUp(){
		var event = new StreamUpEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] ▶️ %s : Stream started".formatted(USERNAME, STREAMER_USERNAME))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void authorNotFound(){
		var event = new StreamUpEvent(STREAMER_ID, null, null, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] ▶️ %s : Stream started".formatted(USERNAME, UNKNOWN_STREAMER))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onStreamDown(){
		var event = new StreamDownEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] ⏹️ %s : Stream stopped".formatted(USERNAME, STREAMER_USERNAME))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onEventCreated(){
		var title = "MyTitle";
		var subEvent = mock(Event.class);
		
		when(subEvent.getTitle()).thenReturn(title);
		when(subEvent.getCreatedAt()).thenReturn(ZONED_NOW);
		
		var event = new EventCreatedEvent(streamer, subEvent);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 📑 %s : Prediction created [%s]".formatted(USERNAME, STREAMER_USERNAME, title))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onPredictionMade(){
		var outcomeId = "outcome-id";
		var outcomeName = "Out2";
		var placedPrediction = mock(PlacedPrediction.class);
		var prediction = mock(BettingPrediction.class);
		var subEvent = mock(Event.class);
		var outcome1 = mock(Outcome.class);
		var outcome2 = mock(Outcome.class);
		
		when(placedPrediction.getAmount()).thenReturn(25);
		when(placedPrediction.getOutcomeId()).thenReturn(outcomeId);
		when(placedPrediction.getBettingPrediction()).thenReturn(prediction);
		when(placedPrediction.getPredictedAt()).thenReturn(NOW);
		when(prediction.getEvent()).thenReturn(subEvent);
		when(subEvent.getOutcomes()).thenReturn(List.of(outcome1, outcome2));
		when(outcome1.getId()).thenReturn("bad-id");
		when(outcome2.getId()).thenReturn(outcomeId);
		when(outcome2.getColor()).thenReturn(OutcomeColor.BLUE);
		when(outcome2.getTitle()).thenReturn(outcomeName);
		
		var event = new PredictionMadeEvent(STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🪙 %s : Bet placed [%d | %s: %s]".formatted(USERNAME, STREAMER_USERNAME, 25, "BLUE", outcomeName))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onPredictionMadeUnknownOutcome(){
		var placedPrediction = mock(PlacedPrediction.class);
		var prediction = mock(BettingPrediction.class);
		var subEvent = mock(Event.class);
		var outcome1 = mock(Outcome.class);
		
		when(placedPrediction.getAmount()).thenReturn(25);
		when(placedPrediction.getOutcomeId()).thenReturn("outcome-id");
		when(placedPrediction.getBettingPrediction()).thenReturn(prediction);
		when(placedPrediction.getPredictedAt()).thenReturn(NOW);
		when(prediction.getEvent()).thenReturn(subEvent);
		when(subEvent.getOutcomes()).thenReturn(List.of(outcome1));
		when(outcome1.getId()).thenReturn("bad-id");
		
		var event = new PredictionMadeEvent(STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🪙 %s : Bet placed [%d | %s]".formatted(USERNAME, STREAMER_USERNAME, 25, "UnknownOutcome"))
				.chatId(TARGET_USERNAME)
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
		
		var event = new PredictionResultEvent(STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction, predictionResultData);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🧧 %s : Bet result [%s | +%d]".formatted(USERNAME, STREAMER_USERNAME, "WIN", 40))
				.chatId(TARGET_USERNAME)
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
		
		var event = new PredictionResultEvent(STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction, predictionResultData);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🧧 %s : Bet result [%s | %d]".formatted(USERNAME, STREAMER_USERNAME, "REFUND", 0))
				.chatId(TARGET_USERNAME)
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
		
		var event = new PredictionResultEvent(STREAMER_ID, STREAMER_USERNAME, streamer, null, predictionResultData);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🧧 %s : Bet result [%s | Unknown final gain, obtained %+d points]".formatted(USERNAME, STREAMER_USERNAME, "WIN", 56))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onMinerStarted(){
		var version = "test-version";
		var commit = "test-commit";
		var branch = "test-branch";
		
		var event = new MinerStartedEvent(version, commit, branch, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] ✅ : Miner started with version %s [%s - %s]".formatted(USERNAME, version, commit, branch))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onStreamerAdded(){
		var event = new StreamerAddedEvent(streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] ➕ %s : Streamer added".formatted(USERNAME, STREAMER_USERNAME))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onStreamerRemoved(){
		var event = new StreamerRemovedEvent(streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] ➖ %s : Streamer removed".formatted(USERNAME, STREAMER_USERNAME))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onStreamerUnknown(){
		var event = new StreamerUnknownEvent(STREAMER_USERNAME, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] ❌ %s : Streamer unknown".formatted(USERNAME, STREAMER_USERNAME))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onDropClaim(){
		var name = "drop-name";
		var drop = mock(TimeBasedDrop.class);
		when(drop.getName()).thenReturn(name);
		
		var event = new DropClaimEvent(drop, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🎁 : Drop available [%s]".formatted(USERNAME, name))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onDropClaimed(){
		var name = "drop-name";
		var drop = mock(TimeBasedDrop.class);
		when(drop.getName()).thenReturn(name);
		
		var event = new DropClaimedEvent(drop, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🎁 : Drop claimed [%s]".formatted(USERNAME, name))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onDropClaimedChannel(){
		var event = new DropClaimedChannelEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🎁 : Drop claimed on channel %s".formatted(USERNAME, STREAMER_USERNAME))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onDropProgressChannel(){
		var event = new DropProgressChannelEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW, 26);
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] 🎁 : Drop progress on channel %s : %s%%".formatted(USERNAME, STREAMER_USERNAME, "26"))
				.chatId(TARGET_USERNAME)
				.build());
	}
	
	@Test
	void onLoginRequired(){
		var event = new LoginRequiredEvent(NOW, "message");
		event.setMiner(miner);
		var webhook = tested.createSimpleMessage(event, messageEventConfiguration, TARGET_USERNAME);
		
		assertThat(webhook).isEqualTo(Message.builder()
				.text("[%s] ⚠️ : message".formatted(USERNAME))
				.chatId(TARGET_USERNAME)
				.build());
	}
}