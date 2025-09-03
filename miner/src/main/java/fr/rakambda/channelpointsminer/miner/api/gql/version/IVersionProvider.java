package fr.rakambda.channelpointsminer.miner.api.gql.version;

import org.jspecify.annotations.NonNull;

public interface IVersionProvider{
	@NonNull
	String getVersion() throws VersionException;
}
