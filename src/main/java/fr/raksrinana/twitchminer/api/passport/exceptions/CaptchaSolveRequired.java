package fr.raksrinana.twitchminer.api.passport.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * Error while login in, a captcha challenge has been requested.
 */
public class CaptchaSolveRequired extends LoginException{
	public CaptchaSolveRequired(@Nullable Integer errorCode, @Nullable String description){
		super(errorCode, description);
	}
}
