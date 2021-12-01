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
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AbstractLogEvent implements ILogEvent{
	protected static final int COLOR_INFO = Color.CYAN.getRGB();
	protected static final int COLOR_PREDICTION = Color.PINK.getRGB();
	protected static final int COLOR_POINTS_WON = Color.GREEN.getRGB();
	protected static final int COLOR_POINTS_LOST = Color.RED.getRGB();
	
	@EqualsAndHashCode.Exclude
	private final ThreadLocal<NumberFormat> numberFormatLocal = ThreadLocal.withInitial(() -> {
		var formatter = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
		formatter.setMaximumFractionDigits(2);
		return formatter;
	});
	
	@Getter
	@NotNull
	private final IMiner miner;
	
	@NotNull
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
	
	@NotNull
	@Override
	public Webhook getAsWebhookMessage(){
		return Webhook.builder().content(getWebhookContent()).build();
	}
	
	@NotNull
	protected String getWebhookContent(){
		return "[%s] %s : %s".formatted(
				miner.getUsername(),
				getEmoji(),
				getWebhookMessage());
	}
	
	@Override
	@NotNull
	public Optional<String> getStreamerUsername(){
		return getStreamer().map(Streamer::getUsername);
	}
	
	@NotNull
	@Override
	public Optional<Streamer> getStreamer(){
		return Optional.empty();
	}
	
	@Nullable
	private Author getEmbedAuthor(){
		var username = getStreamerUsername();
		if(username.isEmpty()){
			return null;
		}
		
		var author = Author.builder().name(username.get());
		
		getStreamer().ifPresent(s -> author
				.iconUrl(s.getProfileImage().orElse(null))
				.url(s.getChannelUrl()));
		
		return author.build();
	}
	
	@NotNull
	public String millify(int value, boolean includeSign){
		var sign = (includeSign && value > 0) ? "+" : "";
		return sign + numberFormatLocal.get().format(value);
	}
	
	@NotNull
	protected abstract String getEmoji();
	
	@NotNull
	protected String getWebhookMessage(){
		return getAsLog();
	}
	
	protected abstract int getEmbedColor();
	
	@NotNull
	protected abstract String getEmbedDescription();
	
	@NotNull
	protected Collection<? extends Field> getEmbedFields(){
		return List.of();
	}
}
