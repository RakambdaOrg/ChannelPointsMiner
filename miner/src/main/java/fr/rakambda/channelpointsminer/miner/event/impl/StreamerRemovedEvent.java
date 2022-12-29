package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamerRemovedEvent extends AbstractLoggableStreamerEvent{
	public StreamerRemovedEvent(@NotNull IMiner miner, @NotNull Streamer streamer, @NotNull Instant instant){
		super(miner, streamer, instant);
	}
	
	@Override
	@NotNull
	public String getConsoleLogFormat(){
		return "Streamer removed";
	}
	
	@Override
	@NotNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} {streamer} : Streamer removed";
	}
	
	@Override
	@NotNull
	protected String getColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "➖";
	}
}
