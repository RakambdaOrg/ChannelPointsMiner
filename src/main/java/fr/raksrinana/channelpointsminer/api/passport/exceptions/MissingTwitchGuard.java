package fr.raksrinana.channelpointsminer.api.passport.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * Error while login in, TwitchGuard code is missing.
 */
public class MissingTwitchGuard extends LoginException{
	public MissingTwitchGuard(int statusCode, @Nullable Integer errorCode, @Nullable String description){
		super(statusCode, errorCode, description);
	}
}
