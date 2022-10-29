package fr.rakambda.channelpointsminer.miner.api.gql.version;

import org.jetbrains.annotations.NotNull;

public interface IVersionProvider{
	@NotNull
	String getVersion() throws VersionException;
}
