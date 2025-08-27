package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ErrorEvent extends AbstractLoggableEvent{
	private final String category;
	private final String message;
	private final Throwable throwable;
	
	public ErrorEvent(@NotNull String category, @NotNull String message, @Nullable Throwable throwable, @NotNull Instant instant){
		super(instant);
		this.category = category;
		this.message = message;
		this.throwable = throwable;
	}
	
	public ErrorEvent(@NotNull String category, @NotNull String message){
		this(category, message, null, TimeFactory.now());
	}
	
	public ErrorEvent(@NotNull String category, @NotNull String message, @Nullable Throwable throwable){
		this(category, message, throwable, TimeFactory.now());
	}
	
	@Override
	@NotNull
	public String getConsoleLogFormat(){
		return "Error received : [{category}] {message} {throwable}";
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.MESSAGE.equals(key)){
			return message;
		}
		if(EventVariableKey.CATEGORY.equals(key)){
			return category;
		}
		if(EventVariableKey.THROWABLE.equals(key)){
			return Objects.isNull(throwable) ? "" : ExceptionUtils.getStackTrace(throwable);
		}
		return super.lookup(key);
	}
	
	@Override
	@NotNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} : Error received : [{category}] {message} {throwable}";
	}
	
	@Override
	@NotNull
	protected String getColor(){
		return COLOR_WARN;
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "😵";
	}
}
