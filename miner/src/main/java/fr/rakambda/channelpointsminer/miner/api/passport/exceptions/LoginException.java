package fr.rakambda.channelpointsminer.miner.api.passport.exceptions;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

/**
 * Error while login in.
 */
@Getter
public class LoginException extends Exception{
	public LoginException(String message){
		super(message);
	}
	
	public LoginException(String message, Throwable cause){
		super(message, cause);
	}
	
	public LoginException(int statusCode, @Nullable Integer errorCode, @Nullable String description){
		this("Failed to login (%d)[%d]: %s".formatted(
				statusCode,
				errorCode == null ? -1 : errorCode,
				description
		));
	}
}
