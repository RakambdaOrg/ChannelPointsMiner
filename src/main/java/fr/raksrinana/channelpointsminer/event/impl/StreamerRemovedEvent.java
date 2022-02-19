package fr.raksrinana.channelpointsminer.event.impl;

import fr.raksrinana.channelpointsminer.event.AbstractStreamerEvent;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamerRemovedEvent extends AbstractStreamerEvent{
	public StreamerRemovedEvent(@NotNull IMiner miner, @NotNull Streamer streamer, @NotNull Instant instant){
		super(miner, streamer, instant);
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Streamer removed";
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "➖";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NotNull
	protected String getEmbedDescription(){
		return "Streamer removed";
	}
}
