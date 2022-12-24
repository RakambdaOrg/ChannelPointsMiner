package fr.rakambda.channelpointsminer.miner.api.passport.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * Error while login in, a captcha challenge has been requested.
 */
public class CaptchaSolveRequired extends LoginException{
	public CaptchaSolveRequired(int statusCode, @Nullable Integer errorCode, @Nullable String description){
		super(statusCode, errorCode, description);
	}
}
