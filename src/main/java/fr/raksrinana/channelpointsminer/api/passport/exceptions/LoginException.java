package fr.raksrinana.channelpointsminer.api.passport.exceptions;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * Error while login in.
 */
@Getter
public class LoginException extends Exception{
	public LoginException(int statusCode, @Nullable Integer errorCode, @Nullable String description){
		super("Failed to login (%d)[%d]: %s".formatted(
				statusCode,
				errorCode == null ? -1 : errorCode,
				description
		));
	}
}
