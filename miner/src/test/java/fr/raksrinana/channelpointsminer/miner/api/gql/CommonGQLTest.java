package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.IntegrityError;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
public class CommonGQLTest extends AbstractGQLTest{
	private static final String RAID_ID = "raid-id";
	
	@Test
	void invalidCredentials(){
		expectValidRequestWithIntegrityOk(401, "api/gql/invalidAuth.json");
		
		assertThrows(RuntimeException.class, () -> tested.joinRaid(RAID_ID));
		
		verifyAll();
	}
	
	@Test
	void invalidRequest(){
		expectValidRequestOkWithIntegrityOk("api/gql/invalidRequest.json");
		
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
	void integrityIsNotQueriedTwice(){
		expectValidRequestOkWithIntegrityOk("api/gql/joinRaid.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		verifyAll();
		reset();
		
		expectValidRequestOk("api/gql/joinRaid.json");
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		verifyAll();
	}
	
	@Test
	void integrityIsRefreshed(){
		setupIntegrityWillNeedRefresh();
		expectValidRequestOk("api/gql/joinRaid.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		verifyAll();
		reset();
		
		setupIntegrityOk();
		expectValidRequestOk("api/gql/joinRaid.json");
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		verifyAll();
	}
	
	@Test
	void integrityNotSuccess(){
		setupIntegrityStatus(500);
		
		var thrown = assertThrows(RuntimeException.class, () -> tested.joinRaid(RAID_ID));
		
		assertThat(thrown)
				.hasCauseInstanceOf(IntegrityError.class)
				.hasCause(new IntegrityError(500, "Http code is not a success"));
		verifyAll();
	}
	
	@Test
	void integrityNoToken(){
		setupIntegrityNoToken();
		
		var thrown = assertThrows(RuntimeException.class, () -> tested.joinRaid(RAID_ID));
		
		assertThat(thrown)
				.hasCauseInstanceOf(IntegrityError.class)
				.hasCause(new IntegrityError(200, "error-message"));
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"c6a332a86d1087fbbb1a8623aa01bd1313d2386e7c63be60fdb2d1901f01a4ae\",\"version\":1}},\"operationName\":\"JoinRaid\",\"variables\":{\"input\":{\"raidID\":\"%s\"}}}".formatted(RAID_ID);
	}
}
