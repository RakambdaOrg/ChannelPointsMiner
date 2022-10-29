package fr.rakambda.channelpointsminer.miner.api.gql.gql;

import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityData;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import fr.rakambda.channelpointsminer.miner.tests.TestUtils;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import static kong.unirest.core.HttpMethod.POST;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
@ParallelizableTest
public abstract class AbstractGQLTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String CLIENT_SESSION_ID = "client-session-id";
	private static final String X_DEVICE_ID = "x-device-id";
	private static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
	private static final String DEFAULT_CLIENT_VERSION = "ef928475-9403-42f2-8a34-55784bd08e16";
	
	protected GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	protected IIntegrityProvider integrityProvider;
	@Mock
	private IntegrityData integrityData;
	
	private UnirestMock unirest;
	private String currentIntegrityToken;
	private String currentClientVersion;
	
	protected void setupIntegrityOk(){
		currentIntegrityToken = "integrity-token";
		when(integrityData.getToken()).thenReturn("integrity-token");
	}
	
	protected abstract String getValidRequest();
	
	protected void verifyAll(){
		unirest.verifyAll();
	}
	
	protected void reset(){
		unirest.reset();
	}
	
	protected void expectValidRequestOkWithIntegrityOk(String responseBody){
		expectBodyRequestOkWithIntegrityOk(getValidRequest(), responseBody);
	}
	
	protected void expectBodyRequestOkWithIntegrityOk(String requestBody, String responseBody){
		setupIntegrityOk();
		expectBodyRequestOk(requestBody, responseBody);
	}
	
	protected void expectBodyRequestOk(String requestBody, String responseBody){
		expectGqlRequest(requestBody, 200, responseBody);
	}
	
	private void expectGqlRequest(String requestBody, int responseStatus, String responseBody){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.header("Client-Integrity", currentIntegrityToken)
				.header("Client-ID", CLIENT_ID)
				.header("Client-Session-Id", CLIENT_SESSION_ID)
				.header("Client-Version", currentClientVersion)
				.header("X-Device-Id", X_DEVICE_ID)
				.body(requestBody)
				.thenReturn(responseBody == null ? null : TestUtils.getAllResourceContent(responseBody))
				.withStatus(responseStatus);
	}
	
	protected void expectValidRequestFailedIntegrity(){
		expectBodyRequestOk(getValidRequest(), "api/gql/gql/error_failedIntegrity.json");
	}
	
	protected void setupIntegrityException() throws IntegrityException{
		currentIntegrityToken = "integrity-refresh";
		when(integrityProvider.getIntegrity()).thenThrow(new IntegrityException(500, "For tests"));
	}
	
	protected void expectValidRequestWithIntegrityOk(int responseStatus, String responseBody){
		setupIntegrityOk();
		expectGqlRequest(getValidRequest(), responseStatus, responseBody);
	}
	
	@BeforeEach
	void setUp(UnirestMock unirest) throws IntegrityException{
		this.unirest = unirest;
		
		currentClientVersion = DEFAULT_CLIENT_VERSION;
		
		lenient().when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
		lenient().when(twitchLogin.getTwitchClient()).thenReturn(TwitchClient.WEB);
		lenient().when(integrityProvider.getIntegrity()).thenReturn(integrityData);
		lenient().when(integrityData.getClientSessionId()).thenReturn(CLIENT_SESSION_ID);
		lenient().when(integrityData.getXDeviceId()).thenReturn(X_DEVICE_ID);
		lenient().when(integrityData.getClientVersion()).thenReturn(DEFAULT_CLIENT_VERSION);
		
		tested = new GQLApi(twitchLogin, unirest.getUnirestInstance(), integrityProvider);
	}
}
