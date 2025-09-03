package fr.rakambda.channelpointsminer.miner.api.gql.integrity;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Error while getting integrity token.
 */
public class IntegrityException extends Exception{
	public IntegrityException(int statusCode, @Nullable String description){
		this("Failed to get integrity token. Status code is " + statusCode + " : " + description);
	}
	
	public IntegrityException(@NonNull String description){
		super(description);
	}
	
	public IntegrityException(String message, Throwable cause){
		super(message, cause);
	}
}
