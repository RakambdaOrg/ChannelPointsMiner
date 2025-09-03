package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.TimeBasedDrop;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.time.Instant;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class DropClaimEvent extends AbstractLoggableEvent{
	private final TimeBasedDrop drop;
	
	public DropClaimEvent(@NonNull TimeBasedDrop drop, @NonNull Instant instant){
		super(instant);
		this.drop = drop;
	}
	
	@Override
	@NonNull
	public String getConsoleLogFormat(){
		return "Drop available [{drop_name}]";
	}
	
	@Override
	@NonNull
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
	@NonNull
	public Map<String, String> getEmbedFields(){
		return Map.of("Name", EventVariableKey.DROP_NAME);
	}
	
	@Override
	@NonNull
	protected String getColor(){
		return COLOR_INFO;
	}
	
	@Override
	@NonNull
	protected String getEmoji(){
		return "🎁";
	}
}
