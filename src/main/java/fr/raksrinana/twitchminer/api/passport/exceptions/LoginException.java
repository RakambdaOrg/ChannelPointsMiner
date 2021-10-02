package fr.raksrinana.twitchminer.api.passport.exceptions;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * Error while login in.
 */
@Getter
public class LoginException extends Exception{
	private final Integer errorCode;
	
	public LoginException(@Nullable Integer errorCode, @Nullable String description){
		super("Failed to login (%d): %s".formatted(errorCode, description));
		this.errorCode = errorCode;
	}
}
