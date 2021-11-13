package fr.raksrinana.channelpointsminer.log;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.api.discord.data.Author;
import fr.raksrinana.channelpointsminer.api.discord.data.Webhook;
import fr.raksrinana.channelpointsminer.api.ws.data.message.*;
import fr.raksrinana.channelpointsminer.api.ws.data.message.claimavailable.ClaimAvailableData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.eventcreated.EventCreatedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.Balance;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsspent.PointsSpentData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionmade.PredictionMadeData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionresult.PredictionResultData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.*;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscordLoggerHandlerMessageTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-name";
	
	private DiscordLoggerHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private DiscordApi discordApi;
	@Mock
	private Streamer streamer;
	@Mock
	private Topic topic;
	
	private Author author;
	
	@BeforeEach
	void setUp() throws MalformedURLException{
		tested = new DiscordLoggerHandler(miner, discordApi, false);
		
		var streamerProfileUrl = new URL("https://streamer-image");
		var channelUrl = new URL("https://streamer");
		
		author = Author.builder()
				.name(STREAMER_USERNAME)
				.url(channelUrl)
				.iconUrl(streamerProfileUrl)
				.build();
		
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
		lenient().when(streamer.getProfileImage()).thenReturn(Optional.of(streamerProfileUrl));
		lenient().when(streamer.getChannelUrl()).thenReturn(channelUrl);
		
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void onClaimAvailable(){
		var claimAvailable = mock(ClaimAvailable.class);
		var data = mock(ClaimAvailableData.class);
		var claim = mock(Claim.class);
		
		when(claimAvailable.getData()).thenReturn(data);
		when(data.getClaim()).thenReturn(claim);
		when(claim.getChannelId()).thenReturn(STREAMER_ID);
		
		tested.handle(topic, claimAvailable);
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("🎫 %s : Claim available".formatted(STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onPointsEarned(){
		var pointsEarned = mock(PointsEarned.class);
		var data = mock(PointsEarnedData.class);
		var pointGain = mock(PointGain.class);
		
		when(pointsEarned.getData()).thenReturn(data);
		when(data.getPointGain()).thenReturn(pointGain);
		when(data.getChannelId()).thenReturn(STREAMER_ID);
		when(pointGain.getTotalPoints()).thenReturn(25);
		when(pointGain.getReasonCode()).thenReturn(PointReasonCode.CLAIM);
		
		tested.handle(topic, pointsEarned);
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("💰 %s : Points earned [%+d | %s]".formatted(STREAMER_USERNAME, 25, "CLAIM"))
				.build());
	}
	
	@Test
	void onPointsSpent(){
		var pointsSpent = mock(PointsSpent.class);
		var data = mock(PointsSpentData.class);
		var balance = mock(Balance.class);
		
		when(pointsSpent.getData()).thenReturn(data);
		when(data.getBalance()).thenReturn(balance);
		when(balance.getChannelId()).thenReturn(STREAMER_ID);
		when(balance.getBalance()).thenReturn(25);
		
		tested.handle(topic, pointsSpent);
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("💸 %s : Points spent [new balance %d]".formatted(STREAMER_USERNAME, 25))
				.build());
	}
	
	@Test
	void onStreamUp(){
		var streamUp = mock(StreamUp.class);
		
		tested.handle(topic, streamUp);
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("▶️ %s : Stream started".formatted(STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void authorNotFound(){
		var streamUp = mock(StreamUp.class);
		
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		tested.handle(topic, streamUp);
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("▶️ %s : Stream started".formatted("UnknownStreamer"))
				.build());
	}
	
	@Test
	void onStreamDown(){
		var streamDown = mock(StreamDown.class);
		
		tested.handle(topic, streamDown);
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("⏹️ %s : Stream stopped".formatted(STREAMER_USERNAME))
				.build());
	}
	
	@Test
	void onEventCreated(){
		var title = "MyTitle";
		var eventCreated = mock(EventCreated.class);
		var eventCreatedData = mock(EventCreatedData.class);
		var event = mock(Event.class);
		
		when(eventCreated.getData()).thenReturn(eventCreatedData);
		when(eventCreatedData.getEvent()).thenReturn(event);
		when(event.getTitle()).thenReturn(title);
		
		tested.handle(topic, eventCreated);
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("📑 %s : New prediction [%s]".formatted(STREAMER_USERNAME, title))
				.build());
	}
	
	@Test
	void onPredictionMade(){
		var predictionMade = mock(PredictionMade.class);
		var predictionMadeData = mock(PredictionMadeData.class);
		var prediction = mock(Prediction.class);
		
		when(predictionMade.getData()).thenReturn(predictionMadeData);
		when(predictionMadeData.getPrediction()).thenReturn(prediction);
		when(prediction.getChannelId()).thenReturn(STREAMER_ID);
		when(prediction.getPoints()).thenReturn(25);
		
		tested.handle(topic, predictionMade);
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("🪙 %s : Bet placed [%d]".formatted(STREAMER_USERNAME, 25))
				.build());
	}
	
	@Test
	void onPredictionResult(){
		var predictionResult = mock(PredictionResult.class);
		var predictionResultData = mock(PredictionResultData.class);
		var prediction = mock(Prediction.class);
		var result = mock(PredictionResultPayload.class);
		
		when(predictionResult.getData()).thenReturn(predictionResultData);
		when(predictionResultData.getPrediction()).thenReturn(prediction);
		when(prediction.getChannelId()).thenReturn(STREAMER_ID);
		when(prediction.getResult()).thenReturn(result);
		when(result.getType()).thenReturn(PredictionResultType.WIN);
		when(result.getPointsWon()).thenReturn(56);
		
		tested.handle(topic, predictionResult);
		
		verify(discordApi).sendMessage(Webhook.builder()
				.content("🧧 %s : Prediction result [%s | +%d]".formatted(STREAMER_USERNAME, "WIN", 56))
				.build());
	}
}