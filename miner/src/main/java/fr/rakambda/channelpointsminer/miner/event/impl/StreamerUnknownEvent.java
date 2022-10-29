package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.discord.data.Field;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamerUnknownEvent extends AbstractLoggableStreamerEvent{
	public StreamerUnknownEvent(@NotNull IMiner miner, @NotNull String streamerUsername, @NotNull Instant instant){
		super(miner, "-1", streamerUsername, null, instant);
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Streamer unknown";
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "❌";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NotNull
	protected String getEmbedDescription(){
		return "Streamer unknown";
	}
	
	@NotNull
	@Override
	protected Collection<? extends Field> getEmbedFields(){
		return List.of(Field.builder().name("Username").value(getStreamerUsername().orElseThrow()).build());
	}
}
