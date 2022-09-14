package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.IntegrityError;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMock;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
public class CommonGQLTest extends AbstractGQLTest{
	private static final String RAID_ID = "raid-id";
	
	@InjectMocks
	private GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
	}
	
	@Test
	void invalidCredentials(UnirestMock unirest){
		expectValidRequestWithIntegrityOk(unirest, 401, "api/gql/invalidAuth.json");
		
		assertThrows(RuntimeException.class, () -> tested.joinRaid(RAID_ID));
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidRequest(UnirestMock unirest){
		expectValidRequestOkWithIntegrityOk(unirest, "api/gql/invalidRequest.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void invalidResponse(UnirestMock unirest){
		expectValidRequestWithIntegrityOk(unirest, 500, null);
		
		assertThat(tested.joinRaid(RAID_ID)).isEmpty();
		
		unirest.verifyAll();
	}
	
	@Test
	void integrityIsNotQueriedTwice(UnirestMock unirest){
		expectValidRequestOkWithIntegrityOk(unirest, "api/gql/joinRaid.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		unirest.verifyAll();
		unirest.reset();
		
		expectValidRequestOk(unirest, "api/gql/joinRaid.json");
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		unirest.verifyAll();
	}
	
	@Test
	void integrityIsRefreshed(UnirestMock unirest){
		setupIntegrityWillNeedRefresh(unirest);
		expectValidRequestOk(unirest, "api/gql/joinRaid.json");
		
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		unirest.verifyAll();
		unirest.reset();
		
		setupIntegrityOk(unirest);
		expectValidRequestOk(unirest, "api/gql/joinRaid.json");
		assertThat(tested.joinRaid(RAID_ID)).isNotEmpty();
		unirest.verifyAll();
	}
	
	@Test
	void integrityNotSuccess(UnirestMock unirest){
		setupIntegrityStatus(unirest, 500);
		
		var thrown = assertThrows(RuntimeException.class, () -> tested.joinRaid(RAID_ID));
		
		assertThat(thrown)
				.hasCauseInstanceOf(IntegrityError.class)
				.hasCause(new IntegrityError(500, "Http code is not a success"));
		unirest.verifyAll();
	}
	
	@Test
	void integrityNoToken(UnirestMock unirest){
		setupIntegrityNoToken(unirest);
		
		var thrown = assertThrows(RuntimeException.class, () -> tested.joinRaid(RAID_ID));
		
		assertThat(thrown)
				.hasCauseInstanceOf(IntegrityError.class)
				.hasCause(new IntegrityError(200, "error-message"));
		unirest.verifyAll();
	}
	
	@Override
	protected String getValidRequest(){
		return "{\"extensions\":{\"persistedQuery\":{\"sha256Hash\":\"c6a332a86d1087fbbb1a8623aa01bd1313d2386e7c63be60fdb2d1901f01a4ae\",\"version\":1}},\"operationName\":\"JoinRaid\",\"variables\":{\"input\":{\"raidID\":\"%s\"}}}".formatted(RAID_ID);
	}
}
