package fr.raksrinana.channelpointsminer.event.impl;

import fr.raksrinana.channelpointsminer.event.AbstractStreamerEvent;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ClaimAvailableEvent extends AbstractStreamerEvent{
	public ClaimAvailableEvent(@NotNull IMiner miner, @NotNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer){
		super(miner, streamerId, streamerUsername, streamer);
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
