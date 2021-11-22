package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamUpLogEvent extends AbstractStreamerLogEvent{
	public StreamUpLogEvent(@NotNull IMiner miner, @Nullable Streamer streamer){
		super(miner, streamer);
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
