package fr.raksrinana.channelpointsminer.miner.event.impl;

import fr.raksrinana.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamerAddedEvent extends AbstractLoggableStreamerEvent{
	public StreamerAddedEvent(@NotNull IMiner miner, @NotNull Streamer streamer, @NotNull Instant instant){
		super(miner, streamer, instant);
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Streamer added";
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "➕";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NotNull
	protected String getEmbedDescription(){
		return "Streamer added";
	}
}
