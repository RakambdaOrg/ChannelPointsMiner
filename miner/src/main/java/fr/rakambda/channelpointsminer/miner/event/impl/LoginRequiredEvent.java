package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableEvent;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@ToString
public class LoginRequiredEvent extends AbstractLoggableEvent{
	public LoginRequiredEvent(@NotNull Instant instant){
		super(instant);
	}
	
	@Override
	@NotNull
	public String getConsoleLogFormat(){
		return "Login required for account {username}";
	}
	
	@Override
	@NotNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} : Login required";
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
