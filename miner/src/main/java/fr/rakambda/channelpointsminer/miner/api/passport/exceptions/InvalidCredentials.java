package fr.rakambda.channelpointsminer.miner.api.passport.exceptions;

import org.jspecify.annotations.Nullable;

/**
 * Error while login in, credentials are invalid.
 */
public class InvalidCredentials extends LoginException{
	public InvalidCredentials(int statusCode, @Nullable Integer errorCode, @Nullable String description){
		super(statusCode, errorCode, description);
	}
}
