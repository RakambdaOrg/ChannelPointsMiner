package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamerRemovedEvent extends AbstractLoggableStreamerEvent{
	public StreamerRemovedEvent(@NonNull Streamer streamer, @NonNull Instant instant){
		super(streamer, instant);
	}
	
	@Override
	@NonNull
	public String getConsoleLogFormat(){
		return "Streamer removed";
	}
	
	@Override
	@NonNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} {streamer} : Streamer removed";
	}
	
	@Override
	@NonNull
	protected String getColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NonNull
	protected String getEmoji(){
		return "➖";
	}
}
