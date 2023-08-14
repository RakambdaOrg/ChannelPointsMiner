package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.Instant;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class DropProgressChannelEvent extends AbstractLoggableStreamerEvent{
	private final String progress;
	
	public DropProgressChannelEvent(@NotNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NotNull Instant instant, float progress){
		super(streamerId, streamerUsername, streamer, instant);
		this.progress = "%.2f".formatted(progress);
	}
	
	@Override
	@NotNull
	public String getConsoleLogFormat(){
		return "Drop progress on channel {streamer} : {drop_progress}%";
	}
	
	@Override
	@NotNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} : Drop progress on channel {streamer} : {drop_progress}%";
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.DROP_PROGRESS.equals(key)){
			return progress;
		}
		return super.lookup(key);
	}
	
	@Override
	@NotNull
	public Map<String, String> getEmbedFields(){
		return Map.of("Progress", EventVariableKey.DROP_PROGRESS);
	}
	
	@Override
	@NotNull
	protected String getColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "🎁";
	}
}
