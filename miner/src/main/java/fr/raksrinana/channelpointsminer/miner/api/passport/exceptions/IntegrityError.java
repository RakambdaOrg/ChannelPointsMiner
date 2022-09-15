package fr.raksrinana.channelpointsminer.miner.api.passport.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * Error while getting integrity token.
 */
public class IntegrityError extends Exception{
	public IntegrityError(int statusCode, @Nullable String description){
		super("Failed to get integrity token. Status code is " + statusCode + " : " + description);
	}
}
