package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.api.discord.data.*;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AbstractLogEvent implements ILogEvent{
	protected static final int COLOR_INFO = Color.CYAN.getRGB();
	protected static final int COLOR_PREDICTION = Color.PINK.getRGB();
	protected static final int COLOR_POINTS_WON = Color.GREEN.getRGB();
	protected static final int COLOR_POINTS_LOST = Color.RED.getRGB();
	
	private static final String UNKNOWN_STREAMER = "UnknownStreamer";
	
	@Getter
	@NotNull
	private final IMiner miner;
	@Nullable
	private final Streamer streamer;
	
	@Override
	public Optional<Streamer> getStreamer(){
		return Optional.ofNullable(streamer);
	}
	
	@Override
	public Webhook getAsWebhookMessage(){
		return Webhook.builder().content("[%s] %s %s : %s".formatted(
						miner.getUsername(),
						getEmoji(),
						getStreamer().map(Streamer::getUsername).orElse(UNKNOWN_STREAMER),
						getWebhookMessage()))
				.build();
	}
	
	@Override
	public Webhook getAsWebhookEmbed(){
		var embed = Embed.builder()
				.author(getEmbedAuthor())
				.footer(Footer.builder().text(miner.getUsername()).build())
				.color(getEmbedColor())
				.description(getEmbedDescription())
				.fields(getEmbedFields())
				.build();
		return Webhook.builder()
				.embeds(List.of(embed))
				.build();
	}
	
	@Nullable
	private Author getEmbedAuthor(){
		if(Objects.isNull(streamer)){
			return null;
		}
		return Author.builder()
				.name(streamer.getUsername())
				.iconUrl(streamer.getProfileImage().orElse(null))
				.url(streamer.getChannelUrl())
				.build();
	}
	
	protected abstract String getEmoji();
	
	protected String getWebhookMessage(){
		return getAsLog();
	}
	
	protected abstract int getEmbedColor();
	
	protected abstract String getEmbedDescription();
	
	protected Collection<? extends Field> getEmbedFields(){
		return List.of();
	}
}
