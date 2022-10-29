package fr.rakambda.channelpointsminer.miner.api.gql.version;

import org.jetbrains.annotations.Nullable;

public class VersionException extends Exception{
	public VersionException(int statusCode, @Nullable String description){
		super("Failed to get version. Status code is " + statusCode + " : " + description);
	}
}
