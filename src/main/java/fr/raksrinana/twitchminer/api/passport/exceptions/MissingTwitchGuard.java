package fr.raksrinana.twitchminer.api.passport.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * Error while login in, TwitchGuard code is missing.
 */
public class MissingTwitchGuard extends LoginException{
	public MissingTwitchGuard(@Nullable Integer errorCode, @Nullable String description){
		super(errorCode, description);
	}
}
