package fr.raksrinana.channelpointsminer.miner.event.impl;

import fr.raksrinana.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamUpEvent extends AbstractLoggableStreamerEvent{
	public StreamUpEvent(@NotNull IMiner miner, @NotNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NotNull Instant instant){
		super(miner, streamerId, streamerUsername, streamer, instant);
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Stream started";
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "▶️";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NotNull
	protected String getEmbedDescription(){
		return "Stream started";
	}
}
