package fr.rakambda.channelpointsminer.miner.event;

import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.awt.*;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AbstractLoggableEvent implements IEvent, ILoggableEvent{
	protected static final String COLOR_INFO = Integer.toString(Color.CYAN.getRGB());
	protected static final String COLOR_WARN = Integer.toString(Color.ORANGE.getRGB());
	protected static final String COLOR_PREDICTION = Integer.toString(Color.PINK.getRGB());
	protected static final String COLOR_POINTS_WON = Integer.toString(Color.GREEN.getRGB());
	protected static final String COLOR_POINTS_LOST = Integer.toString(Color.RED.getRGB());
	
	@EqualsAndHashCode.Exclude
	private final ThreadLocal<NumberFormat> numberFormatLocal = ThreadLocal.withInitial(() -> {
		var formatter = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
		formatter.setMaximumFractionDigits(2);
		return formatter;
	});
	
	@Getter
	@NonNull
	private final Instant instant;
	
	@Setter
	@Getter
	private IMiner miner;
	
	@NonNull
	public String millify(int value, boolean includeSign){
		var sign = (includeSign && value > 0) ? "+" : "";
		return sign + numberFormatLocal.get().format(value);
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.USERNAME.equals(key)){
			return getMiner().getUsername();
		}
		if(EventVariableKey.EMOJI.equals(key)){
			return getEmoji();
		}
		if(EventVariableKey.COLOR.equals(key)){
			return getColor();
		}
		return null;
	}
	
	@Override
	@NonNull
	public Map<String, String> getEmbedFields(){
		return Map.of();
	}
	
	@NonNull
	protected abstract String getColor();
	
	@NonNull
	protected abstract String getEmoji();
}
