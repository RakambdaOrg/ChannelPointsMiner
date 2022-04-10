package fr.raksrinana.channelpointsminer.miner.event.impl;

import fr.raksrinana.channelpointsminer.miner.event.AbstractStreamerEvent;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ClaimAvailableEvent extends AbstractStreamerEvent{
	public ClaimAvailableEvent(@NotNull IMiner miner, @NotNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NotNull Instant instant){
		super(miner, streamerId, streamerUsername, streamer, instant);
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Claim available";
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "🎫";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_INFO;
	}
	
	@Override
	
	@NotNull
	protected String getEmbedDescription(){
		return "Claim available";
	}
}
