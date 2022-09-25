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
import static kong.unirest.core.HttpMethod.GET;
import static kong.unirest.core.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class HttpIntegrityProviderTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String CLIENT_SESSION_ID = "client-session-id";
	private static final String X_DEVICE_ID = "x-device-id";
	private static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
	private static final String DEFAULT_CLIENT_VERSION = "ef928475-9403-42f2-8a34-55784bd08e16";
	
	private HttpIntegrityProvider tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	private UnirestMock unirestMock;
	private String currentIntegrityToken;
	private Instant currentIntegrityExpiration;
	private String currentClientVersion;
	
	@Test
	void integrityIsNotQueriedTwice() throws IntegrityException{
		setupClientVersionOk();
		setupIntegrityOk();
		
		var integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
		unirestMock.reset();
		
		integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
	}
	
	private void setupClientVersionOk(){
		setupClientVersionOk(currentClientVersion);
	}
	
	private void setupIntegrityOk(){
		currentIntegrityToken = "integrity-token";
		currentIntegrityExpiration = Instant.ofEpochMilli(9999999999999L);
		expectIntegrityRequest(200, "api/gql/integrity/integrity_ok.json");
	}
	
	private void assertValidData(IntegrityData integrity){
		assertThat(integrity).isNotNull();
		assertThat(integrity.getToken()).isEqualTo(currentIntegrityToken);
		assertThat(integrity.getExpiration()).isEqualTo(currentIntegrityExpiration);
		assertThat(integrity.getClientVersion()).isEqualTo(currentClientVersion);
		assertThat(integrity.getClientSessionId()).isEqualTo(CLIENT_SESSION_ID);
		assertThat(integrity.getXDeviceId()).isEqualTo(X_DEVICE_ID);
	}
	
	private void setupClientVersionOk(String twitchVersion){
		currentClientVersion = twitchVersion;
		expectClientVersionRequest(200, "window.__twilightBuildID=\"%s\";".formatted(currentClientVersion));
	}
	
	private void expectIntegrityRequest(int responseStatus, String responseBody){
		unirestMock.expect(POST, "https://gql.twitch.tv/integrity")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.header("Client-ID", CLIENT_ID)
				.header("Client-Session-Id", CLIENT_SESSION_ID)
				.header("Client-Version", currentClientVersion)
				.header("X-Device-Id", X_DEVICE_ID)
				.thenReturn(responseBody == null ? null : TestUtils.getAllResourceContent(responseBody))
				.withStatus(responseStatus);
	}
	
	private void expectClientVersionRequest(int responseStatus, String responseBody){
		unirestMock.expect(GET, "https://www.twitch.tv")
				.thenReturn(responseBody)
				.withStatus(responseStatus);
	}
	
	@Test
	void integrityIsRefreshed() throws IntegrityException{
		setupClientVersionOk();
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
	
	private void setupIntegrityWillNeedRefresh(){
		currentIntegrityToken = "integrity-refresh";
		currentIntegrityExpiration = Instant.ofEpochMilli(0L);
		expectIntegrityRequest(200, "api/gql/integrity/integrity_needRefresh.json");
	}
	
	@Test
	void integrityIsInvalidated() throws IntegrityException{
		setupClientVersionOk();
		setupIntegrityOk();
		
		var integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
		unirestMock.reset();
		
		tested.invalidate();
		setupClientVersionOk();
		setupIntegrityOk();
		
		integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
	}
	
	@Test
	void integrityNotSuccess(){
		setupClientVersionOk();
		expectIntegrityRequest(500, null);
		
		var thrown = assertThrows(IntegrityException.class, () -> tested.getIntegrity());
		assertThat(thrown).hasMessage("Failed to get integrity token. Status code is 500 : Http code is not a success");
		unirestMock.verifyAll();
	}
	
	@Test
	void integrityNoToken(){
		setupClientVersionOk();
		expectIntegrityRequest(200, "api/gql/integrity/integrity_noToken.json");
		
		var thrown = assertThrows(IntegrityException.class, () -> tested.getIntegrity());
		assertThat(thrown).hasMessage("Failed to get integrity token. Status code is 200 : error-message");
		unirestMock.verifyAll();
	}
	
	@Test
	void clientVersionErrorResponse() throws IntegrityException{
		expectClientVersionRequest(500, null);
		setupIntegrityOk();
		
		var integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
	}
	
	@Test
	void clientVersionNullBody() throws IntegrityException{
		expectClientVersionRequest(200, null);
		setupIntegrityOk();
		
		var integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
	}
	
	@Test
	void clientVersionNotMatchingBody() throws IntegrityException{
		expectClientVersionRequest(200, "not what we want");
		setupIntegrityOk();
		
		var integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
	}
	
	@Test
	void clientVersionChanged() throws IntegrityException{
		setupClientVersionOk("0202fcd9-207a-4659-956c-ed2030260de0");
		setupIntegrityOk();
		
		var integrity = tested.getIntegrity();
		assertValidData(integrity);
		unirestMock.verifyAll();
	}
	
	@BeforeEach
	void setUp(UnirestMock unirestMock){
		this.unirestMock = unirestMock;
		currentClientVersion = DEFAULT_CLIENT_VERSION;
		
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
		
		tested = new HttpIntegrityProvider(twitchLogin, unirestMock.getUnirestInstance(), CLIENT_SESSION_ID, X_DEVICE_ID);
	}
}