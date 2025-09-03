package fr.rakambda.channelpointsminer.miner.api.gql.integrity;

import org.jspecify.annotations.NonNull;
import java.util.Optional;

public interface IIntegrityProvider{
	void invalidate();
	
	@NonNull
	Optional<IntegrityData> getIntegrity() throws IntegrityException;
}
