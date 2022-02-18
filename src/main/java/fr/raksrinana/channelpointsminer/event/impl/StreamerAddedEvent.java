package fr.raksrinana.channelpointsminer.event.impl;

import fr.raksrinana.channelpointsminer.event.AbstractStreamerEvent;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamerAddedEvent extends AbstractStreamerEvent{
	public StreamerAddedEvent(@NotNull IMiner miner, @NotNull Streamer streamer){
		super(miner, streamer);
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
