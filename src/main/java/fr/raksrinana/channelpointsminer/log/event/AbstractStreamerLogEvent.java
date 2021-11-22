package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AbstractStreamerLogEvent extends AbstractLogEvent{
	private static final String UNKNOWN_STREAMER = "UnknownStreamer";
	
	@EqualsAndHashCode.Exclude
	private final ThreadLocal<NumberFormat> numberFormatLocal = ThreadLocal.withInitial(() -> {
		var formatter = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
		formatter.setMaximumFractionDigits(2);
		return formatter;
	});
	
	@Nullable
	private final Streamer streamer;
	
	public AbstractStreamerLogEvent(@NotNull IMiner miner, @Nullable Streamer streamer){
		super(miner);
		this.streamer = streamer;
	}
	
	@Override
	@NotNull
	protected String getWebhookContent(){
		return "[%s] %s %s : %s".formatted(
				getMiner().getUsername(),
				getEmoji(),
				getStreamerUsername().orElse(UNKNOWN_STREAMER),
				getWebhookMessage());
	}
	
	@NotNull
	@Override
	public Optional<Streamer> getStreamer(){
		return Optional.ofNullable(streamer);
	}
}
