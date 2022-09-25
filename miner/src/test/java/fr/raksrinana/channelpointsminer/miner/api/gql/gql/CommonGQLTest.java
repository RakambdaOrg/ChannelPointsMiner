package fr.raksrinana.channelpointsminer.miner.api.gql.gql;

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
	void integrityIsNotQueriedTwice(){
		expectValidRequestOkWithIntegrityOk("api/gql/gql/joinRaid.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		verifyAll();
		reset();
		
		expectValidRequestOk("api/gql/gql/joinRaid.json");
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		verifyAll();
	}
	
	@Test
	void integrityIsRefreshed(){
		setupTwitchVersionOk();
		setupIntegrityWillNeedRefresh();
		expectValidRequestOk("api/gql/gql/joinRaid.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		verifyAll();
		reset();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/joinRaid.json");
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		verifyAll();
	}
	
	@Test
	void integrityIsInvalidatedOnError(){
		setupIntegrityOk();
		expectValidRequestOk("api/gql/gql/error_failedIntegrity.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isEmpty();
		verifyAll();
		reset();
		
		expectValidRequestOkWithIntegrityOk("api/gql/gql/joinRaid.json");
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		verifyAll();
	}
	
	@Test
	void integrityNotSuccess(){
		setupTwitchVersionOk();
		setupIntegrityStatus(500);
		
		var thrown = assertThrows(RuntimeException.class, () -> tested.joinRaid(RAID_ID));
		
		assertThat(thrown)
				.hasCauseInstanceOf(IntegrityError.class)
				.hasCause(new IntegrityError(500, "Http code is not a success"));
		verifyAll();
	}
	
	@Test
	void integrityNoToken(){
		setupTwitchVersionOk();
		setupIntegrityNoToken();
		
		var thrown = assertThrows(RuntimeException.class, () -> tested.joinRaid(RAID_ID));
		
		assertThat(thrown)
				.hasCauseInstanceOf(IntegrityError.class)
				.hasCause(new IntegrityError(200, "error-message"));
		verifyAll();
	}
	
	@Test
	void clientVersionErrorResponse(){
		expectTwitchVersionRequest(500, null);
		setupIntegrityOkWithoutTwitchVersionOk();
		expectValidRequestOk("api/gql/gql/joinRaid.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isPresent();
		
		verifyAll();
	}
	
	@Test
	void clientVersionNullBody(){
		expectTwitchVersionRequest(200, null);
		setupIntegrityOkWithoutTwitchVersionOk();
		expectValidRequestOk("api/gql/gql/joinRaid.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isPresent();
		
		verifyAll();
	}
	
	@Test
	void clientVersionNotMatchingBody(){
		expectTwitchVersionRequest(200, "not what we want");
		setupIntegrityOkWithoutTwitchVersionOk();
		expectValidRequestOk("api/gql/gql/joinRaid.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isPresent();
		
		verifyAll();
	}
	
	@Test
	void clientVersionChanged(){
		setupTwitchVersionOk("0202fcd9-207a-4659-956c-ed2030260de0");
		setupIntegrityOkWithoutTwitchVersionOk();
		expectValidRequestOk("api/gql/gql/joinRaid.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isPresent();
		
		verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"c6a332a86d1087fbbb1a8623aa01bd1313d2386e7c63be60fdb2d1901f01a4ae\",\"version\":1}},\"operationName\":\"JoinRaid\",\"variables\":{\"input\":{\"raidID\":\"%s\"}}}".formatted(RAID_ID);
	}
}
