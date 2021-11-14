package fr.raksrinana.channelpointsminer.log;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.api.discord.data.*;
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
	private static final String UNKNOWN_STREAMER = "UnknownStreamer";
	
	private final IMiner miner;
	private final DiscordApi discordApi;
	private final boolean useEmbeds;
	
	@Override
	public void onClaimAvailable(@NotNull Topic topic, @NotNull ClaimAvailable message){
		var streamer = miner.getStreamerById(message.getData().getClaim().getChannelId());
		try(var ignored = LogContext.with(miner).withStreamer(streamer.orElse(null))){
			var webhook = Webhook.builder();
			
			if(useEmbeds){
				var embed = createEmbed(streamer.orElse(null))
						.color(COLOR_INFO)
						.description("Claim available")
						.build();
				webhook.embeds(List.of(embed));
			}
			else{
				webhook.content("[%s] 🎫 %s : Claim available".formatted(
						miner.getUsername(),
						streamer.map(Streamer::getUsername).orElse(UNKNOWN_STREAMER)));
			}
			
			discordApi.sendMessage(webhook.build());
		}
	}
	
	@Override
	public void onEventCreated(@NotNull Topic topic, @NotNull EventCreated message){
		var streamer = miner.getStreamerById(topic.getTarget());
		try(var ignored = LogContext.with(miner).withStreamer(streamer.orElse(null))){
			var title = message.getData().getEvent().getTitle();
			var webhook = Webhook.builder();
			
			if(useEmbeds){
				var embed = createEmbed(streamer.orElse(null))
						.color(COLOR_PREDICTION)
						.description("Prediction created")
						.field(Field.builder().name("Title").value(title).build())
						.build();
				webhook.embeds(List.of(embed));
			}
			else{
				webhook.content("[%s] 📑 %s : New prediction [%s]".formatted(
						miner.getUsername(),
						streamer.map(Streamer::getUsername).orElse(UNKNOWN_STREAMER),
						title));
			}
			
			discordApi.sendMessage(webhook.build());
		}
	}
	
	@Override
	public void onPointsEarned(@NotNull Topic topic, @NotNull PointsEarned message){
		var pointsEarnedData = message.getData();
		var streamer = miner.getStreamerById(pointsEarnedData.getChannelId());
		try(var ignored = LogContext.with(miner).withStreamer(streamer.orElse(null))){
			var pointGain = pointsEarnedData.getPointGain();
			var points = pointGain.getTotalPoints();
			var reasonCode = pointGain.getReasonCode();
			var webhook = Webhook.builder();
			
			if(useEmbeds){
				var embed = createEmbed(streamer.orElse(null))
						.color(COLOR_POINTS_WON)
						.description("Points earned")
						.field(Field.builder().name("Earned").value(Integer.toString(points)).build())
						.field(Field.builder().name("Reason").value(reasonCode.toString()).build())
						.build();
				webhook.embeds(List.of(embed));
			}
			else{
				webhook.content("[%s] 💰 %s : Points earned [%+d | %s]".formatted(
						miner.getUsername(),
						streamer.map(Streamer::getUsername).orElse(UNKNOWN_STREAMER),
						points,
						reasonCode));
			}
			
			discordApi.sendMessage(webhook.build());
		}
	}
	
	@Override
	public void onPointsSpent(@NotNull Topic topic, @NotNull PointsSpent message){
		var balance = message.getData().getBalance();
		var streamer = miner.getStreamerById(balance.getChannelId());
		try(var ignored = LogContext.with(miner).withStreamer(streamer.orElse(null))){
			var webhook = Webhook.builder();
			
			if(useEmbeds){
				var embed = createEmbed(streamer.orElse(null))
						.color(COLOR_POINTS_LOST)
						.description("Points spent")
						.field(Field.builder().name("New balance").value(Integer.toString(balance.getBalance())).build())
						.build();
				webhook.embeds(List.of(embed));
			}
			else{
				webhook.content("[%s] 💸 %s : Points spent [new balance %d]".formatted(
						miner.getUsername(),
						streamer.map(Streamer::getUsername).orElse(UNKNOWN_STREAMER),
						balance.getBalance()));
			}
			
			discordApi.sendMessage(webhook.build());
		}
	}
	
	@Override
	public void onStreamDown(@NotNull Topic topic, @NotNull StreamDown message){
		var streamer = miner.getStreamerById(topic.getTarget());
		try(var ignored = LogContext.with(miner).withStreamer(streamer.orElse(null))){
			var webhook = Webhook.builder();
			
			if(useEmbeds){
				var embed = createEmbed(streamer.orElse(null))
						.color(COLOR_INFO)
						.description("Stream stopped")
						.build();
				webhook.embeds(List.of(embed));
			}
			else{
				webhook.content("[%s] ⏹️ %s : Stream stopped".formatted(
						miner.getUsername(),
						streamer.map(Streamer::getUsername).orElse(UNKNOWN_STREAMER)));
			}
			
			discordApi.sendMessage(webhook.build());
		}
	}
	
	@Override
	public void onStreamUp(@NotNull Topic topic, @NotNull StreamUp message){
		var streamer = miner.getStreamerById(topic.getTarget());
		try(var ignored = LogContext.with(miner).withStreamer(streamer.orElse(null))){
			var webhook = Webhook.builder();
			
			if(useEmbeds){
				var embed = createEmbed(streamer.orElse(null))
						.color(COLOR_INFO)
						.description("Stream started")
						.build();
				webhook.embeds(List.of(embed));
			}
			else{
				webhook.content("[%s] ▶️ %s : Stream started".formatted(
						miner.getUsername(),
						streamer.map(Streamer::getUsername).orElse(UNKNOWN_STREAMER)));
			}
			
			discordApi.sendMessage(webhook.build());
		}
	}
	
	@Override
	public void onPredictionMade(@NotNull Topic topic, @NotNull PredictionMade message){
		var prediction = message.getData().getPrediction();
		var streamer = miner.getStreamerById(prediction.getChannelId());
		try(var ignored = LogContext.with(miner).withStreamer(streamer.orElse(null))){
			var points = prediction.getPoints();
			var webhook = Webhook.builder();
			
			if(useEmbeds){
				var embed = createEmbed(streamer.orElse(null))
						.color(COLOR_PREDICTION)
						.description("Bet placed")
						.field(Field.builder().name("Points placed").value(Integer.toString(points)).build())
						.build();
				webhook.embeds(List.of(embed));
			}
			else{
				webhook.content("[%s] 🪙 %s : Bet placed [%d]".formatted(
						miner.getUsername(),
						streamer.map(Streamer::getUsername).orElse(UNKNOWN_STREAMER),
						points));
			}
			
			discordApi.sendMessage(webhook.build());
		}
	}
	
	@Override
	public void onPredictionResult(@NotNull Topic topic, @NotNull PredictionResult message){
		var prediction = message.getData().getPrediction();
		var streamer = miner.getStreamerById(prediction.getChannelId());
		try(var ignored = LogContext.with(miner).withStreamer(streamer.orElse(null))){
			var result = Optional.ofNullable(prediction.getResult());
			var type = result.map(PredictionResultPayload::getType).map(Enum::toString).orElse("Unknown");
			var points = result.map(PredictionResultPayload::getPointsWon).map("%+d"::formatted).orElse("Unknown");
			var webhook = Webhook.builder();
			
			if(useEmbeds){
				var embed = createEmbed(streamer.orElse(null))
						.color(COLOR_PREDICTION)
						.description("Prediction result")
						.field(Field.builder().name("Type").value(type).build())
						.field(Field.builder().name("Points gained").value(points).build())
						.build();
				webhook.embeds(List.of(embed));
			}
			else{
				webhook.content("[%s] 🧧 %s : Prediction result [%s | %s]".formatted(
						miner.getUsername(),
						streamer.map(Streamer::getUsername).orElse(UNKNOWN_STREAMER),
						type,
						points));
			}
			
			discordApi.sendMessage(webhook.build());
		}
	}
	
	@NotNull
	private Embed.EmbedBuilder createEmbed(@Nullable Streamer streamer){
		var embed = Embed.builder()
				.footer(Footer.builder().text(miner.getUsername()).build());
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
