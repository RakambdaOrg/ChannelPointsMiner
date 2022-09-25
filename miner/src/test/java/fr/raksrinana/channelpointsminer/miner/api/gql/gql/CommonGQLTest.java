package fr.raksrinana.channelpointsminer.miner.api.gql.gql;

import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
public class CommonGQLTest extends AbstractGQLTest{
	private static final String RAID_ID = "raid-id";
	
	@Test
	void invalidCredentials(){
		expectValidRequestWithIntegrityOk(401, "api/gql/gql/error_invalidAuth.json");
		
		assertThrows(RuntimeException.class, () -> tested.joinRaid(RAID_ID));
		
		verifyAll();
	}
	
	@Test
	void invalidRequest(){
		expectValidRequestOkWithIntegrityOk("api/gql/gql/error_invalidRequest.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isEmpty();
		
		verifyAll();
	}
	
	@Test
	void expectedError(){
		expectValidRequestOkWithIntegrityOk("api/gql/gql/error_expected.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isEmpty();
		
		verifyAll();
	}
	
	@Test
	void invalidResponse(){
		expectValidRequestWithIntegrityOk(500, null);
		
		assertThat(tested.joinRaid(RAID_ID)).isEmpty();
		
		verifyAll();
	}
	
	@Test
	void integrityIsInvalidatedOnError(){
		setupIntegrityOk();
		expectValidRequestFailedIntegrity();
		
		assertThat(tested.joinRaid(RAID_ID)).isEmpty();
		verifyAll();
		reset();
		verify(integrityProvider).invalidate();
	}
	
	@Test
	void integrityException() throws IntegrityException{
		setupIntegrityException();
		
		var thrown = assertThrows(RuntimeException.class, () -> tested.joinRaid(RAID_ID));
		
		assertThat(thrown)
				.hasCauseInstanceOf(IntegrityException.class)
				.hasCause(new IntegrityException(500, "For tests"));
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"c6a332a86d1087fbbb1a8623aa01bd1313d2386e7c63be60fdb2d1901f01a4ae\",\"version\":1}},\"operationName\":\"JoinRaid\",\"variables\":{\"input\":{\"raidID\":\"%s\"}}}".formatted(RAID_ID);
	}
}
