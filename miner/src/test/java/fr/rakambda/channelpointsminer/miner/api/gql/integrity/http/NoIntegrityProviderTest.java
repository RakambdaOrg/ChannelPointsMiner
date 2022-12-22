package fr.rakambda.channelpointsminer.miner.api.gql.integrity.http;

import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NoIntegrityProviderTest{
	private static final NoIntegrityProvider tested = new NoIntegrityProvider();
	
	@Test
	void getIntegrity() throws IntegrityException{
		assertThat(tested.getIntegrity()).isEmpty();
	}
}