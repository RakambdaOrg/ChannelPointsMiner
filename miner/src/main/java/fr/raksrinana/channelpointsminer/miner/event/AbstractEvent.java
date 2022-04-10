package fr.raksrinana.channelpointsminer.miner.event;

import fr.raksrinana.channelpointsminer.miner.api.discord.data.Author;
import fr.raksrinana.channelpointsminer.miner.api.discord.data.Embed;
import fr.raksrinana.channelpointsminer.miner.api.discord.data.Field;
import fr.raksrinana.channelpointsminer.miner.api.discord.data.Footer;
import fr.raksrinana.channelpointsminer.miner.api.discord.data.Webhook;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AbstractEvent implements IEvent, ILoggableEvent{
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
	@Getter
	@NotNull
	private final Instant instant;
	
	@NotNull
	public String millify(int value, boolean includeSign){
		var sign = (includeSign && value > 0) ? "+" : "";
		return sign + numberFormatLocal.get().format(value);
	}
	
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
	
	@NotNull
	protected abstract String getEmoji();
	
	@NotNull
	protected String getWebhookMessage(){
		return getAsLog();
	}
	
	@Nullable
	protected Author getEmbedAuthor(){
		return null;
	}
	
	protected abstract int getEmbedColor();
	
	@NotNull
	protected abstract String getEmbedDescription();
	
	@NotNull
	protected Collection<? extends Field> getEmbedFields(){
		return List.of();
	}
}
