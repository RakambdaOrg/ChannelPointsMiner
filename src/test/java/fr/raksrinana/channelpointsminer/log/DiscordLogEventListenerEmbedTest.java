package fr.raksrinana.channelpointsminer.log;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.api.discord.data.*;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import static java.awt.Color.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscordLogEventListenerEmbedTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-name";
	private static final String USERNAME = "username";
	
	private DiscordLogEventListener tested;
	
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
		tested = new DiscordLogEventListener(discordApi, true);
		
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
	void onClaimAvailable(){
		tested.onLogEvent(new ClaimAvailableLogEvent(miner, streamer));
		
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
	void onPointsEarned(){
		var data = mock(PointsEarnedData.class);
		var pointGain = mock(PointGain.class);
		
		when(data.getPointGain()).thenReturn(pointGain);
		when(pointGain.getTotalPoints()).thenReturn(25);
		when(pointGain.getReasonCode()).thenReturn(PointReasonCode.CLAIM);
		
		tested.onLogEvent(new PointsEarnedLogEvent(miner, streamer, data));
		
		verify(discordApi).sendMessage(Webhook.builder()
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(GREEN.getRGB())
						.description("Points earned")
						.field(Field.builder().name("Points").value("25").build())
						.field(Field.builder().name("Reason").value("CLAIM").build())
						.build()))
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
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(RED.getRGB())
						.description("Points spent")
						.field(Field.builder().name("New balance").value("25").build())
						.build()))
				.build());
	}
	
	@Test
	void onStreamUp(){
		tested.onLogEvent(new StreamUpLogEvent(miner, streamer));
		
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
		tested.onLogEvent(new StreamUpLogEvent(miner, null));
		
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
		tested.onLogEvent(new StreamDownLogEvent(miner, streamer));
		
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
		
		tested.onLogEvent(new EventCreatedLogEvent(miner, streamer, event));
		
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
		when(prediction.getEvent()).thenReturn(event);
		when(event.getOutcomes()).thenReturn(List.of(outcome1, outcome2));
		when(outcome1.getId()).thenReturn("bad-id");
		when(outcome2.getId()).thenReturn(outcomeId);
		when(outcome2.getColor()).thenReturn(OutcomeColor.BLUE);
		when(outcome2.getTitle()).thenReturn(outcomeName);
		
		tested.onLogEvent(new PredictionMadeLogEvent(miner, streamer, placedPrediction));
		
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
		when(prediction.getEvent()).thenReturn(event);
		when(event.getOutcomes()).thenReturn(List.of(outcome1));
		when(outcome1.getId()).thenReturn("bad-id");
		
		tested.onLogEvent(new PredictionMadeLogEvent(miner, streamer, placedPrediction));
		
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
		when(prediction.getResult()).thenReturn(result);
		when(result.getType()).thenReturn(PredictionResultType.WIN);
		when(result.getPointsWon()).thenReturn(56);
		
		tested.onLogEvent(new PredictionResultLogEvent(miner, streamer, placedPrediction, predictionResultData));
		
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
				.embeds(List.of(Embed.builder()
						.author(author)
						.footer(footer)
						.color(PINK.getRGB())
						.description("Bet result")
						.field(Field.builder().name("Type").value("WIN").build())
						.field(Field.builder().name("Points gained").value("Unknown final gain, obtained 56 points").build())
						.build()))
				.build());
	}
	
	@Test
	void onMinerStarted(){
		var version = "test-version";
		var commit = "test-commit";
		var branch = "test-branch";
		tested.onLogEvent(new MinerStartedLogEvent(miner, version, commit, branch));
		
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
}