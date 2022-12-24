package fr.rakambda.channelpointsminer.miner.api.gql.integrity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Error while getting integrity token.
 */
public class IntegrityException extends Exception{
	public IntegrityException(int statusCode, @Nullable String description){
		this("Failed to get integrity token. Status code is " + statusCode + " : " + description);
	}
	
	public IntegrityException(@NotNull String description){
		super(description);
	}
	
	public IntegrityException(String message, Throwable cause){
		super(message, cause);
	}
}
