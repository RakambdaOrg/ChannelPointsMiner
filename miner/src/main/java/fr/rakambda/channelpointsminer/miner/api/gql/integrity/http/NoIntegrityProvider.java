package fr.rakambda.channelpointsminer.miner.api.gql.integrity.http;

import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityData;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
public class NoIntegrityProvider implements IIntegrityProvider{
	@Override
	@NonNull
	public Optional<IntegrityData> getIntegrity(){
		return Optional.empty();
	}
	
	@Override
	public void invalidate(){
	}
}
