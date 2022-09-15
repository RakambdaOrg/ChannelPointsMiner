package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import fr.raksrinana.channelpointsminer.miner.tests.TestUtils;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMock;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMockExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import static kong.unirest.core.HttpMethod.POST;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
@ParallelizableTest
public abstract class AbstractGQLTest{
	private static final String ACCESS_TOKEN = "access-token";
	private static final String CLIENT_SESSION_ID = "client-session-id";
	private static final String X_DEVICE_ID = "x-device-id";
	private static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
	private static final String CLIENT_VERSION = "97087acf-5eca-40dd-9a1b-ee0e771c3d3f";
	
	protected GQLApi tested;
	
	@Mock
	private TwitchLogin twitchLogin;
	
	private UnirestMock unirest;
	private String currentIntegrityToken;
	
	@BeforeEach
	void setUp(UnirestMock unirest){
		this.unirest = unirest;
		
		when(twitchLogin.getAccessToken()).thenReturn(ACCESS_TOKEN);
		
		tested = new GQLApi(twitchLogin, unirest.getUnirestInstance(), CLIENT_SESSION_ID, X_DEVICE_ID);
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
	
	protected void setupIntegrityOk(){
		currentIntegrityToken = "integrity-token";
		expectIntegrityRequest(200, "api/gql/integrity_ok.json");
	}
	
	protected void expectBodyRequestOk(String requestBody, String responseBody){
		expectGqlRequest(requestBody, 200, responseBody);
	}
	
	private void expectIntegrityRequest(int responseStatus, String responseBody){
		unirest.expect(POST, "https://gql.twitch.tv/integrity")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.header("Client-ID", CLIENT_ID)
				.header("Client-Session-Id", CLIENT_SESSION_ID)
				.header("Client-Version", CLIENT_VERSION)
				.header("X-Device-Id", X_DEVICE_ID)
				.thenReturn(responseBody == null ? null : TestUtils.getAllResourceContent(responseBody))
				.withStatus(responseStatus);
	}
	
	private void expectGqlRequest(String requestBody, int responseStatus, String responseBody){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.header("Client-Integrity", currentIntegrityToken)
				.header("Client-ID", CLIENT_ID)
				.header("Client-Session-Id", CLIENT_SESSION_ID)
				.header("Client-Version", CLIENT_VERSION)
				.header("X-Device-Id", X_DEVICE_ID)
				.body(requestBody)
				.thenReturn(responseBody == null ? null : TestUtils.getAllResourceContent(responseBody))
				.withStatus(responseStatus);
	}
	
	protected void expectValidRequestOk(String responseBody){
		expectBodyRequestOk(getValidRequest(), responseBody);
	}
	
	protected void expectValidRequestWithIntegrityOk(int responseStatus, String responseBody){
		setupIntegrityOk();
		expectGqlRequest(getValidRequest(), responseStatus, responseBody);
	}
	
	protected void setupIntegrityWillNeedRefresh(){
		currentIntegrityToken = "integrity-refresh";
		expectIntegrityRequest(200, "api/gql/integrity_needRefresh.json");
	}
	
	protected void setupIntegrityNoToken(){
		expectIntegrityRequest(200, "api/gql/integrity_noToken.json");
	}
	
	protected void setupIntegrityStatus(int status){
		expectIntegrityRequest(status, null);
	}
}
