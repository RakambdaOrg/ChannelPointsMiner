package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamerUnknownEvent extends AbstractLoggableStreamerEvent{
	public StreamerUnknownEvent(@NotNull IMiner miner, @NotNull String streamerUsername, @NotNull Instant instant){
		super(miner, "-1", streamerUsername, null, instant);
	}
	
	@Override
	@NotNull
	public String getConsoleLogFormat(){
		return "Streamer unknown";
	}
	
	@Override
	@NotNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} {streamer} : Streamer unknown";
	}
	
	@Override
	@NotNull
	protected String getColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "❌";
	}
	
	@Override
	@NotNull
	public Map<String, String> getEmbedFields(){
		return Map.of("Streamer", EventVariableKey.STREAMER);
	}
}
