package fr.rakambda.channelpointsminer.miner.api.gql.integrity.http;

import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
class NoIntegrityProviderTest{
	private static final NoIntegrityProvider tested = new NoIntegrityProvider();
	
	@Test
	void getIntegrity() throws IntegrityException{
		assertThat(tested.getIntegrity()).isEmpty();
	}
}