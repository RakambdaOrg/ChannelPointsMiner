package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@ToString
public class LoginRequiredEvent extends AbstractLoggableEvent{
	private final String message;
	
	public LoginRequiredEvent(@NotNull Instant instant, @NotNull String message){
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
	@NotNull
	public String getConsoleLogFormat(){
		return "{message} for account {username}";
	}
	
	@Override
	@NotNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} : {message}";
	}
	
	@Override
	@NotNull
	protected String getColor(){
		return COLOR_WARN;
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "⚠️";
	}
}
