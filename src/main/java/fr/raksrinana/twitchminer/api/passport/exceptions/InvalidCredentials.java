package fr.raksrinana.twitchminer.api.passport.exceptions;

import org.jetbrains.annotations.Nullable;
/**
 * Error while login in, credentials are invalid.
 */
public class InvalidCredentials extends LoginException{
	public InvalidCredentials(@Nullable Integer errorCode, @Nullable String description){
		super(errorCode, description);
	}
}
