package fr.rakambda.channelpointsminer.miner.log;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
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
import fr.rakambda.channelpointsminer.miner.event.IEvent;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.PINK;
import static java.awt.Color.RED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class DiscordEventListenerEmbedTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-name";
	private static final String USERNAME = "username";
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
	
	private Author author;
	private Footer footer;
	
	@BeforeEach
	void setUp() throws MalformedURLException{
		tested = new DiscordEventListener(discordApi, true, e -> true);
		
		var streamerProfileUrl = new URL("https://streamer-image");
		var channelUrl = new URL("https://streamer");
		
		author = Author.builder()
				.name(STREAMER_USERNAME)
				.url(channelUrl)
				.iconUrl(streamerProfileUrl)
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
	void notLoggableEventIsIgnored(){
		var event = mock(IEvent.class);
		
		tested.onEvent(event);
		
		verify(discordApi, never()).sendMessage(any());
	}
	
	@Test
	void onClaimAvailable(){
		tested.onEvent(new ClaimAvailableEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Claim available")
						.build()))
				.build());
	}
	
	@Test
	void onClaimMoment(){
		tested.onEvent(new ClaimMomentEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Moment available")
						.build()))
				.build());
	}
	
	@Test
	void onClaimedMoment(){
		tested.onEvent(new ClaimedMomentEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Moment claimed")
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
		
		tested.onEvent(new PointsEarnedEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(GREEN.getRGB())
						.description("Points earned")
						.field(Field.builder().name("Points").value("+25").build())
						.field(Field.builder().name("Reason").value("CLAIM").build())
						.field(Field.builder().name("Balance").value("200").build())
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
		
		tested.onEvent(new PointsEarnedEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(GREEN.getRGB())
						.description("Points earned")
						.field(Field.builder().name("Points").value("+2.5K").build())
						.field(Field.builder().name("Reason").value("CLAIM").build())
						.field(Field.builder().name("Balance").value("12.35M").build())
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
		
		tested.onEvent(new PointsEarnedEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(GREEN.getRGB())
						.description("Points earned")
						.field(Field.builder().name("Points").value("-2.5K").build())
						.field(Field.builder().name("Reason").value("CLAIM").build())
						.field(Field.builder().name("Balance").value("12.35M").build())
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
		
		tested.onEvent(new PointsSpentEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, data));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(RED.getRGB())
						.description("Points spent")
						.field(Field.builder().name("Balance").value("25").build())
						.build()))
				.build());
	}
	
	@Test
	void onStreamUp(){
		tested.onEvent(new StreamUpEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Stream started")
						.build()))
				.build());
	}
	
	@Test
	void authorNotFound(){
		tested.onEvent(new StreamUpEvent(miner, STREAMER_ID, null, null, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Stream started")
						.build()))
				.build());
	}
	
	@Test
	void onStreamDown(){
		tested.onEvent(new StreamDownEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Stream stopped")
						.build()))
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
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("Prediction created")
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
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("Bet placed")
						.field(Field.builder().name("Points placed").value("25").build())
						.field(Field.builder().name("Outcome").value("BLUE: " + outcomeName).build())
						.build()))
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
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("Bet placed")
						.field(Field.builder().name("Points placed").value("25").build())
						.field(Field.builder().name("Outcome").value("UnknownOutcome").build())
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
		
		tested.onEvent(new PredictionResultEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction, predictionResultData));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("Bet result")
						.field(Field.builder().name("Type").value("WIN").build())
						.field(Field.builder().name("Points gained").value("+40").build())
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
		
		tested.onEvent(new PredictionResultEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, placedPrediction, predictionResultData));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("Bet result")
						.field(Field.builder().name("Type").value("REFUND").build())
						.field(Field.builder().name("Points gained").value("0").build())
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
		
		tested.onEvent(new PredictionResultEvent(miner, STREAMER_ID, STREAMER_USERNAME, streamer, null, predictionResultData));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("Bet result")
						.field(Field.builder().name("Type").value("WIN").build())
						.field(Field.builder().name("Points gained").value("Unknown final gain, obtained +56 points").build())
						.build()))
				.build());
	}
	
	@Test
	void onMinerStarted(){
		var version = "test-version";
		var commit = "test-commit";
		var branch = "test-branch";
		tested.onEvent(new MinerStartedEvent(miner, version, commit, branch, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Miner started")
						.field(Field.builder().name("Version").value(version).build())
						.field(Field.builder().name("Commit").value(commit).build())
						.field(Field.builder().name("Branch").value(branch).build())
						.build()))
				.build());
	}
	
	@Test
	void onStreamerAdded(){
		tested.onEvent(new StreamerAddedEvent(miner, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Streamer added")
						.build()))
				.build());
	}
	
	@Test
	void onStreamerRemoved(){
		tested.onEvent(new StreamerRemovedEvent(miner, streamer, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Streamer removed")
						.build()))
				.build());
	}
	
	@Test
	void onStreamerUnknown(){
		tested.onEvent(new StreamerUnknownEvent(miner, STREAMER_USERNAME, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(Author.builder()
								.name(STREAMER_USERNAME)
								.build())
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Streamer unknown")
						.field(Field.builder().name("Username").value(STREAMER_USERNAME).build())
						.build()))
				.build());
	}
	
	@Test
	void onDropClaim(){
		var name = "drop-name";
		var drop = mock(TimeBasedDrop.class);
		when(drop.getName()).thenReturn(name);
		
		tested.onEvent(new DropClaimEvent(miner, drop, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Drop available")
						.field(Field.builder().name("Name").value(name).build())
						.build()))
				.build());
	}
	
	@Test
	void onDropClaimed(){
		var name = "drop-name";
		var drop = mock(TimeBasedDrop.class);
		when(drop.getName()).thenReturn(name);
		
		tested.onEvent(new DropClaimedEvent(miner, drop, NOW));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.footer(footer)
						.color(CYAN.getRGB())
						.description("Drop claimed")
						.field(Field.builder().name("Name").value(name).build())
						.build()))
				.build());
	}
}