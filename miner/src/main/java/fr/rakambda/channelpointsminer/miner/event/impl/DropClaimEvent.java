package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDrop;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class DropClaimEvent extends AbstractLoggableEvent{
	private final TimeBasedDrop drop;
	
	public DropClaimEvent(@NotNull TimeBasedDrop drop, @NotNull Instant instant){
		super(instant);
		this.drop = drop;
	}
	
	@Override
	@NotNull
	public String getConsoleLogFormat(){
		return "Drop available [{drop_name}]";
	}
	
	@Override
	@NotNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} : Drop available [{drop_name}]";
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.DROP_NAME.equals(key)){
			return drop.getName();
		}
		return super.lookup(key);
	}
	
	@Override
	@NotNull
	public Map<String, String> getEmbedFields(){
		return Map.of("Name", EventVariableKey.DROP_NAME);
	}
	
	@Override
	@NotNull
	protected String getColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "🎁";
	}
}
