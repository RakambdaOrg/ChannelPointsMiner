package fr.raksrinana.channelpointsminer.miner.api.gql.integrity;

import org.jetbrains.annotations.Nullable;

/**
 * Error while getting integrity token.
 */
public class IntegrityException extends Exception{
	public IntegrityException(int statusCode, @Nullable String description){
		super("Failed to get integrity token. Status code is " + statusCode + " : " + description);
	}
}
