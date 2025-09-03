package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ClaimAvailableEvent extends AbstractLoggableStreamerEvent{
	public ClaimAvailableEvent(@NonNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NonNull Instant instant){
		super(streamerId, streamerUsername, streamer, instant);
	}
	
	@Override
	@NonNull
	public String getConsoleLogFormat(){
		return "Claim available";
	}
	
	@Override
	@NonNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} {streamer} : Claim available";
	}
	
	@Override
	@NonNull
	protected String getColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NonNull
	protected String getEmoji(){
		return "🎫";
	}
}
