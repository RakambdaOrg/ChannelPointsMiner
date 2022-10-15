package fr.raksrinana.channelpointsminer.miner.api.gql.integrity.http;

import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IntegrityData;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.TestUtils;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMock;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import static kong.unirest.core.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class MobileIntegrityProviderTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String CLIENT_SESSION_ID = "client-session-id";
	private static final String X_DEVICE_ID = "x-device-id";
	private static final String CLIENT_ID = "kd1unb4b3q4t58fwlpcbzcbnm76a8fp";
	private static final String CLIENT_VERSION = "32d439b2-bd5b-4e35-b82a-fae10b04da70";
	private static final String ACCEPT_TYPE = "application/vnd.twitchtv.v3+json";
	private static final String API_CONSUMER_TYPE = "mobile; Android/1309000";
	private static final String X_APP_VERSION = "13.9.0";
	
	private MobileIntegrityProvider tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	private UnirestMock unirestMock;
	private String currentIntegrityToken;
	private Instant currentIntegrityExpiration;
	
	@BeforeEach
	void setUp(UnirestMock unirestMock){
		this.unirestMock = unirestMock;
		
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
		
		tested = new MobileIntegrityProvider(twitchLogin, unirestMock.getUnirestInstance(), CLIENT_SESSION_ID, X_DEVICE_ID);
	}
	
	@Test
	void integrityIsNotQueriedTwice() throws IntegrityException{
		setupIntegrityOk();
		
		var integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
		unirestMock.reset();
		
		integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
	}
	
	@Test
	void integrityIsRefreshed() throws IntegrityException{
		setupIntegrityWillNeedRefresh();
		
		var integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
		unirestMock.reset();
		
		setupIntegrityOk();
		integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
	}
	
	@Test
	void integrityIsInvalidated() throws IntegrityException{
		setupIntegrityOk();
		
		var integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
		unirestMock.reset();
		
		tested.invalidate();
		setupIntegrityOk();
		
		integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
	}
	
	@Test
	void integrityNotSuccess(){
		expectIntegrityRequest(500, null);
		
		var thrown = assertThrows(IntegrityException.class, () -> tested.getIntegrity());
		assertThat(thrown).hasMessage("Failed to get integrity token. Status code is 500 : Http code is not a success");
		unirestMock.verifyAll();
	}
	
	@Test
	void integrityNoToken(){
		expectIntegrityRequest(200, "api/gql/integrity/integrity_noToken.json");
		
		var thrown = assertThrows(IntegrityException.class, () -> tested.getIntegrity());
		assertThat(thrown).hasMessage("Failed to get integrity token. Status code is 200 : error-message");
		unirestMock.verifyAll();
	}
	
	private void assertValidData(IntegrityData integrity){
		assertThat(integrity).isNotNull();
		assertThat(integrity.getToken()).isEqualTo(currentIntegrityToken);
		assertThat(integrity.getExpiration()).isEqualTo(currentIntegrityExpiration);
		assertThat(integrity.getClientVersion()).isEqualTo(CLIENT_VERSION);
		assertThat(integrity.getClientSessionId()).isEqualTo(CLIENT_SESSION_ID);
		assertThat(integrity.getXDeviceId()).isEqualTo(X_DEVICE_ID);
	}
	
	private void setupIntegrityOk(){
		currentIntegrityToken = "integrity-token";
		currentIntegrityExpiration = Instant.ofEpochMilli(9999999999999L);
		expectIntegrityRequest(200, "api/gql/integrity/integrity_ok.json");
	}
	
	private void setupIntegrityWillNeedRefresh(){
		currentIntegrityToken = "integrity-refresh";
		currentIntegrityExpiration = Instant.ofEpochMilli(0L);
		expectIntegrityRequest(200, "api/gql/integrity/integrity_needRefresh.json");
	}
	
	private void expectIntegrityRequest(int responseStatus, String responseBody){
		unirestMock.expect(POST, "https://gql.twitch.tv/integrity")
				.header("Accept", ACCEPT_TYPE)
				.header("Api-Consumer-Type", API_CONSUMER_TYPE)
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.header("Client-ID", CLIENT_ID)
				.header("Client-Session-Id", CLIENT_SESSION_ID)
				.header("Client-Version", CLIENT_VERSION)
				.header("X-Device-Id", X_DEVICE_ID)
				.header("X-App-Version", X_APP_VERSION)
				.thenReturn(responseBody == null ? null : TestUtils.getAllResourceContent(responseBody))
				.withStatus(responseStatus);
	}
}