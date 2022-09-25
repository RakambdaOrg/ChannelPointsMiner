package fr.raksrinana.channelpointsminer.miner.api.gql.integrity;

import org.jetbrains.annotations.NotNull;

public interface IIntegrityProvider{
	void invalidate();
	
	@NotNull
	IntegrityData getIntegrity() throws IntegrityException;
}
