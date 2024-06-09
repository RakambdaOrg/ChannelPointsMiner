package fr.rakambda.channelpointsminer.miner.log.discord;

import fr.rakambda.channelpointsminer.miner.api.discord.data.Author;
import fr.rakambda.channelpointsminer.miner.api.discord.data.Embed;
import fr.rakambda.channelpointsminer.miner.api.discord.data.Field;
import fr.rakambda.channelpointsminer.miner.api.discord.data.Footer;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.PINK;
import static java.awt.Color.RED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class DiscordMessageBuilderEmbedTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-name";
	private static final String USERNAME = "username";
	private static final Instant NOW = Instant.parse("2020-05-17T12:14:20.000Z");
	private static final ZonedDateTime ZONED_NOW = ZonedDateTime.ofInstant(NOW, ZoneId.systemDefault());
	
	@InjectMocks
	private DiscordMessageBuilder tested;
	
	@Mock
	private MessageEventConfiguration messageEventConfiguration;
	@Mock
	private IMiner miner;
	@Mock
	private Streamer streamer;
	@Mock
	private Topic topic;
	
	private Author author;
	private Footer footer;
	
	@BeforeEach
	void setUp() throws MalformedURLException{
		var streamerProfileUrl = URI.create("https://streamer-image").toURL();
		var channelUrl = URI.create("https://streamer").toURL();
		
		author = Author.builder()
				.name(STREAMER_USERNAME)
				.url(channelUrl.toString())
				.iconUrl(streamerProfileUrl.toString())
				.build();
		footer = Footer.builder()
				.text(USERNAME)
				.build();
		
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		lenient().when(miner.getUsername()).thenReturn(USERNAME);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
		lenient().when(streamer.getProfileImage()).thenReturn(Optional.of(streamerProfileUrl));
		lenient().when(streamer.getChannelUrl()).thenReturn(channelUrl);
		
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void onClaimAvailableWithCustomFormat(){
		when(messageEventConfiguration.getFormat()).thenReturn("{streamer} override test");
		
		var event = new ClaimAvailableEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("streamer-name override test")
						.build()))
				.build());
	}
	
	@Test
	void onClaimAvailable(){
		var event = new ClaimAvailableEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] 🎫 streamer-name : Claim available")
						.build()))
				.build());
	}
	
	@Test
	void onClaimMoment(){
		var event = new ClaimMomentEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] \uD83C\uDF96️ streamer-name : Moment available")
						.build()))
				.build());
	}
	
	@Test
	void onClaimedMoment(){
		var event = new ClaimedMomentEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] 🎖️ streamer-name : Moment claimed")
						.build()))
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
		
		var event = new PointsEarnedEvent(STREAMER_ID, STREAMER_USERNAME, streamer, data);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(GREEN.getRGB())
						.description("[username] 💰 streamer-name : Points earned [+25 | CLAIM | 200]")
						.field(Field.builder().name("Balance").value("200").build())
						.field(Field.builder().name("Points").value("+25").build())
						.field(Field.builder().name("Reason").value("CLAIM").build())
						.build()))
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
		
		var event = new PointsEarnedEvent(STREAMER_ID, STREAMER_USERNAME, streamer, data);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(GREEN.getRGB())
						.description("[username] 💰 streamer-name : Points earned [+2.5K | CLAIM | 12.35M]")
						.field(Field.builder().name("Balance").value("12.35M").build())
						.field(Field.builder().name("Points").value("+2.5K").build())
						.field(Field.builder().name("Reason").value("CLAIM").build())
						.build()))
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
		
		var event = new PointsEarnedEvent(STREAMER_ID, STREAMER_USERNAME, streamer, data);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(GREEN.getRGB())
						.description("[username] 💰 streamer-name : Points earned [-2.5K | CLAIM | 12.35M]")
						.field(Field.builder().name("Balance").value("12.35M").build())
						.field(Field.builder().name("Points").value("-2.5K").build())
						.field(Field.builder().name("Reason").value("CLAIM").build())
						.build()))
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
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(RED.getRGB())
						.description("[username] 💸 streamer-name : Points spent [25]")
						.field(Field.builder().name("Balance").value("25").build())
						.build()))
				.build());
	}
	
	@Test
	void onStreamUp(){
		var event = new StreamUpEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] ▶️ streamer-name : Stream started")
						.build()))
				.build());
	}
	
	@Test
	void authorNotFound(){
		var event = new StreamUpEvent(STREAMER_ID, null, null, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(Author.builder().name("UnknownStreamer").build())
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] ▶️ UnknownStreamer : Stream started")
						.build()))
				.build());
	}
	
	@Test
	void onStreamDown(){
		var event = new StreamDownEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] ⏹️ streamer-name : Stream stopped")
						.build()))
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
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("[username] 📑 streamer-name : Prediction created [MyTitle]")
						.field(Field.builder().name("Title").value(title).build())
						.build()))
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
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("[username] 🪙 streamer-name : Bet placed [25 | BLUE: Out2]")
						.field(Field.builder().name("Outcome").value("BLUE: " + outcomeName).build())
						.field(Field.builder().name("Points placed").value("25").build())
						.build()))
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
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("[username] 🪙 streamer-name : Bet placed [25 | UnknownOutcome]")
						.field(Field.builder().name("Outcome").value("UnknownOutcome").build())
						.field(Field.builder().name("Points placed").value("25").build())
						.build()))
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
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("[username] 🧧 streamer-name : Bet result [WIN | +40]")
						.field(Field.builder().name("Points gained").value("+40").build())
						.field(Field.builder().name("Type").value("WIN").build())
						.build()))
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
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("[username] 🧧 streamer-name : Bet result [REFUND | 0]")
						.field(Field.builder().name("Points gained").value("0").build())
						.field(Field.builder().name("Type").value("REFUND").build())
						.build()))
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
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("[username] 🧧 streamer-name : Bet result [WIN | Unknown final gain, obtained +56 points]")
						.field(Field.builder().name("Points gained").value("Unknown final gain, obtained +56 points").build())
						.field(Field.builder().name("Type").value("WIN").build())
						.build()))
				.build());
	}
	
	@Test
	void onMinerStarted(){
		var version = "test-version";
		var commit = "test-commit";
		var branch = "test-branch";
		
		var event = new MinerStartedEvent(version, commit, branch, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] ✅ : Miner started with version test-version [test-commit - test-branch]")
						.field(Field.builder().name("Branch").value(branch).build())
						.field(Field.builder().name("Commit").value(commit).build())
						.field(Field.builder().name("Version").value(version).build())
						.build()))
				.build());
	}
	
	@Test
	void onStreamerAdded(){
		var event = new StreamerAddedEvent(streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] ➕ streamer-name : Streamer added")
						.build()))
				.build());
	}
	
	@Test
	void onStreamerRemoved(){
		var event = new StreamerRemovedEvent(streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] ➖ streamer-name : Streamer removed")
						.build()))
				.build());
	}
	
	@Test
	void onStreamerUnknown(){
		var event = new StreamerUnknownEvent(STREAMER_USERNAME, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(Author.builder()
								.name(STREAMER_USERNAME)
								.build())
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] ❌ streamer-name : Streamer unknown")
						.field(Field.builder().name("Streamer").value(STREAMER_USERNAME).build())
						.build()))
				.build());
	}
	
	@Test
	void onDropClaim(){
		var name = "drop-name";
		var drop = mock(TimeBasedDrop.class);
		when(drop.getName()).thenReturn(name);
		
		var event = new DropClaimEvent(drop, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] 🎁 : Drop available [drop-name]")
						.field(Field.builder().name("Name").value(name).build())
						.build()))
				.build());
	}
	
	@Test
	void onDropClaimed(){
		var name = "drop-name";
		var drop = mock(TimeBasedDrop.class);
		when(drop.getName()).thenReturn(name);
		
		var event = new DropClaimedEvent(drop, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] \uD83C\uDF81 : Drop claimed [drop-name]")
						.field(Field.builder().name("Name").value(name).build())
						.build()))
				.build());
	}
	
	@Test
	void onDropClaimedChannel(){
		var event = new DropClaimedChannelEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] \uD83C\uDF81 : Drop claimed on channel streamer-name")
						.build()))
				.build());
	}
	
	@Test
	void onDropProgressChannel(){
		var event = new DropProgressChannelEvent(STREAMER_ID, STREAMER_USERNAME, streamer, NOW, 20);
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("[username] \uD83C\uDF81 : Drop progress on channel streamer-name : 20%")
						.field(Field.builder().name("Progress").value("20").build())
						.build()))
				.build());
	}
	
	@Test
	void onLoginRequired(){
		var event = new LoginRequiredEvent(NOW, "message");
		event.setMiner(miner);
		var webhook = tested.createEmbedMessage(event, messageEventConfiguration);
		
		assertThat(webhook).isEqualTo(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.footer(footer)
						.color(ORANGE.getRGB())
						.description("[username] ⚠️ : message")
						.build()))
				.build());
	}
}