package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.time.Instant;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamerUnknownEvent extends AbstractLoggableStreamerEvent{
	public StreamerUnknownEvent(@NonNull String streamerUsername, @NonNull Instant instant){
		super("-1", streamerUsername, null, instant);
	}
	
	@Override
	@NonNull
	public String getConsoleLogFormat(){
		return "Streamer unknown";
	}
	
	@Override
	@NonNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} {streamer} : Streamer unknown";
	}
	
	@Override
	@NonNull
	protected String getColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NonNull
	protected String getEmoji(){
		return "❌";
	}
	
	@Override
	@NonNull
	public Map<String, String> getEmbedFields(){
		return Map.of("Streamer", EventVariableKey.STREAMER);
	}
}
