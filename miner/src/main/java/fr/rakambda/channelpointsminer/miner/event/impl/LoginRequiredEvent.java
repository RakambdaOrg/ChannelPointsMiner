package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@ToString
public class LoginRequiredEvent extends AbstractLoggableEvent{
	private final String message;
	
	public LoginRequiredEvent(@NonNull Instant instant, @NonNull String message){
		super(instant);
		this.message = message;
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.MESSAGE.equals(key)){
			return message;
		}
		return super.lookup(key);
	}
	
	@Override
	@NonNull
	public String getConsoleLogFormat(){
		return "{message} for account {username}";
	}
	
	@Override
	@NonNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} : {message}";
	}
	
	@Override
	@NonNull
	protected String getColor(){
		return COLOR_WARN;
	}
	
	@Override
	@NonNull
	protected String getEmoji(){
		return "⚠️";
	}
}
