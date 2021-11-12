package fr.raksrinana.channelpointsminer.log;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.api.discord.data.Author;
import fr.raksrinana.channelpointsminer.api.discord.data.Embed;
import fr.raksrinana.channelpointsminer.api.discord.data.Field;
import fr.raksrinana.channelpointsminer.api.discord.data.Webhook;
import fr.raksrinana.channelpointsminer.api.ws.data.message.*;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PredictionResultPayload;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.handler.HandlerAdapter;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public class DiscordLoggerHandler extends HandlerAdapter{
	private static final int COLOR_INFO = Color.CYAN.getRGB();
	private static final int COLOR_PREDICTION = Color.PINK.getRGB();
	private static final int COLOR_POINTS_WON = Color.GREEN.getRGB();
	private static final int COLOR_POINTS_LOST = Color.RED.getRGB();
	
	private final IMiner miner;
	private final DiscordApi discordApi;
	
	@Override
	public void onClaimAvailable(@NotNull Topic topic, @NotNull ClaimAvailable message){
		var streamer = miner.getStreamerById(message.getData().getClaim().getChannelId()).orElse(null);
		try(var ignored = LogContext.with(streamer)){
			var embed = createEmbedForStreamer(streamer)
					.color(COLOR_INFO)
					.description("Claim available")
					.build();
			discordApi.sendMessage(Webhook.builder()
					.embeds(List.of(embed))
					.build());
		}
	}
	
	@Override
	public void onEventCreated(@NotNull Topic topic, @NotNull EventCreated message){
		var streamer = miner.getStreamerById(topic.getTarget()).orElse(null);
		try(var ignored = LogContext.with(streamer)){
			var embed = createEmbedForStreamer(streamer)
					.color(COLOR_PREDICTION)
					.description("Prediction created")
					.field(Field.builder().name("Title").value(message.getData().getEvent().getTitle()).build())
					.build();
			discordApi.sendMessage(Webhook.builder()
					.embeds(List.of(embed))
					.build());
		}
	}
	
	@Override
	public void onPointsEarned(@NotNull Topic topic, @NotNull PointsEarned message){
		var pointsEarnedData = message.getData();
		var streamer = miner.getStreamerById(pointsEarnedData.getChannelId()).orElse(null);
		try(var ignored = LogContext.with(streamer)){
			var pointGain = pointsEarnedData.getPointGain();
			var embed = createEmbedForStreamer(streamer)
					.color(COLOR_POINTS_WON)
					.description("Points earned")
					.field(Field.builder().name("Earned").value(Integer.toString(pointGain.getTotalPoints())).build())
					.field(Field.builder().name("Reason").value(pointGain.getReasonCode().toString()).build())
					.build();
			discordApi.sendMessage(Webhook.builder()
					.embeds(List.of(embed))
					.build());
		}
	}
	
	@Override
	public void onPointsSpent(@NotNull Topic topic, @NotNull PointsSpent message){
		var balance = message.getData().getBalance();
		var streamer = miner.getStreamerById(balance.getChannelId()).orElse(null);
		try(var ignored = LogContext.with(streamer)){
			var embed = createEmbedForStreamer(streamer)
					.color(COLOR_POINTS_LOST)
					.description("Points spent")
					.field(Field.builder().name("New balance").value(Integer.toString(balance.getBalance())).build())
					.build();
			discordApi.sendMessage(Webhook.builder()
					.embeds(List.of(embed))
					.build());
		}
	}
	
	@Override
	public void onStreamDown(@NotNull Topic topic, @NotNull StreamDown message){
		var streamer = miner.getStreamerById(topic.getTarget()).orElse(null);
		try(var ignored = LogContext.with(streamer)){
			var embed = createEmbedForStreamer(streamer)
					.color(COLOR_INFO)
					.description("Stream stopped")
					.build();
			discordApi.sendMessage(Webhook.builder()
					.embeds(List.of(embed))
					.build());
		}
	}
	
	@Override
	public void onStreamUp(@NotNull Topic topic, @NotNull StreamUp message){
		var streamer = miner.getStreamerById(topic.getTarget()).orElse(null);
		try(var ignored = LogContext.with(streamer)){
			var embed = createEmbedForStreamer(streamer)
					.color(COLOR_INFO)
					.description("Stream started")
					.build();
			discordApi.sendMessage(Webhook.builder()
					.embeds(List.of(embed))
					.build());
		}
	}
	
	@Override
	public void onPredictionMade(@NotNull Topic topic, @NotNull PredictionMade message){
		var prediction = message.getData().getPrediction();
		var streamer = miner.getStreamerById(prediction.getChannelId()).orElse(null);
		try(var ignored = LogContext.with(streamer)){
			var embed = createEmbedForStreamer(streamer)
					.color(COLOR_PREDICTION)
					.description("Bet placed")
					.field(Field.builder().name("Points placed").value(Integer.toString(prediction.getPoints())).build())
					.build();
			discordApi.sendMessage(Webhook.builder()
					.embeds(List.of(embed))
					.build());
		}
	}
	
	@Override
	public void onPredictionResult(@NotNull Topic topic, @NotNull PredictionResult message){
		var prediction = message.getData().getPrediction();
		var streamer = miner.getStreamerById(prediction.getChannelId()).orElse(null);
		try(var ignored = LogContext.with(streamer)){
			var result = Optional.ofNullable(prediction.getResult());
			var embed = createEmbedForStreamer(streamer)
					.color(COLOR_PREDICTION)
					.description("Prediction result")
					.field(Field.builder()
							.name("Type")
							.value(result.map(PredictionResultPayload::getType).map(Enum::toString).orElse("Unknown"))
							.build())
					.field(Field.builder()
							.name("Points gained")
							.value(result.map(PredictionResultPayload::getPointsWon).map(i -> Integer.toString(i)).orElse("Unknown"))
							.build())
					.build();
			discordApi.sendMessage(Webhook.builder()
					.embeds(List.of(embed))
					.build());
		}
	}
	
	@NotNull
	private Embed.EmbedBuilder createEmbedForStreamer(@Nullable Streamer streamer){
		var embed = Embed.builder();
		if(streamer == null){
			return embed;
		}
		return embed.author(Author.builder()
				.name(streamer.getUsername())
				.iconUrl(streamer.getProfileImage().orElse(null))
				.url(streamer.getChannelUrl())
				.build());
	}
}
