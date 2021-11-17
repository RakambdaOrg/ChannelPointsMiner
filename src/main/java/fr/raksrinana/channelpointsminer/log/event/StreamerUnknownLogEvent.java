package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Field;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamerUnknownLogEvent extends AbstractLogEvent{
	private final String username;
	
	public StreamerUnknownLogEvent(@NotNull IMiner miner, @NotNull String username){
		super(miner, null);
		this.username = username;
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Streamer unknown";
	}
	
	@Override
	@NotNull
	public Optional<String> getStreamerUsername(){
		return Optional.of(username);
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
		return List.of(Field.builder().name("Username").value(username).build());
	}
}
