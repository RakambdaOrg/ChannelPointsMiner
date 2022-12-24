package fr.rakambda.channelpointsminer.miner.api.gql.integrity;

import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public interface IIntegrityProvider{
	void invalidate();
	
	@NotNull
	Optional<IntegrityData> getIntegrity() throws IntegrityException;
}
