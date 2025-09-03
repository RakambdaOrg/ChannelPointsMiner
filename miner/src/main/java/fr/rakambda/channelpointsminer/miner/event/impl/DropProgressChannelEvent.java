package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.time.Instant;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class DropProgressChannelEvent extends AbstractLoggableStreamerEvent{
	private final String progress;
	
	public DropProgressChannelEvent(@NonNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NonNull Instant instant, int progress){
		super(streamerId, streamerUsername, streamer, instant);
		this.progress = Integer.toString(progress);
	}
	
	@Override
	@NonNull
	public String getConsoleLogFormat(){
		return "Drop progress on channel {streamer} : {drop_progress}%";
	}
	
	@Override
	@NonNull
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
	@NonNull
	public Map<String, String> getEmbedFields(){
		return Map.of("Progress", EventVariableKey.DROP_PROGRESS);
	}
	
	@Override
	@NonNull
	protected String getColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NonNull
	protected String getEmoji(){
		return "🎁";
	}
}
