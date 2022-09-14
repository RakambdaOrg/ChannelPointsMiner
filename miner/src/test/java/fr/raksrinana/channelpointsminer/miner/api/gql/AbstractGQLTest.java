package fr.raksrinana.channelpointsminer.miner.api.gql;

import fr.raksrinana.channelpointsminer.miner.tests.TestUtils;
import fr.raksrinana.channelpointsminer.miner.tests.UnirestMock;
import static kong.unirest.core.HttpMethod.POST;

public abstract class AbstractGQLTest{
	protected static final String ACCESS_TOKEN = "access-token";
	private static final String INTEGRITY_TOKEN = "integrity-token";
	
	protected void expectValidRequestOkWithIntegrityOk(UnirestMock unirest, String responseBody){
		expectBodyRequestOkWithIntegrityOk(unirest, getValidRequest(), responseBody);
	}
	
	protected void expectBodyRequestOkWithIntegrityOk(UnirestMock unirest, String requestBody, String responseBody){
		setupIntegrityOk(unirest);
		expectBodyRequestOk(unirest, requestBody, responseBody);
	}
	
	protected abstract String getValidRequest();
	
	protected void setupIntegrityOk(UnirestMock unirest){
		unirest.expect(POST, "https://gql.twitch.tv/integrity")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.thenReturn(TestUtils.getAllResourceContent("api/gql/integrity_ok.json"))
				.withStatus(200);
	}
	
	protected void expectBodyRequestOk(UnirestMock unirest, String requestBody, String responseBody){
		expectRequest(unirest, requestBody, 200, responseBody);
	}
	
	private void expectRequest(UnirestMock unirest, String requestBody, int responseStatus, String responseBody){
		unirest.expect(POST, "https://gql.twitch.tv/gql")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.header("Client-Integrity", INTEGRITY_TOKEN)
				.header("Client-ID", "kimne78kx3ncx6brgo4mv6wki5h1ko")
				.body(requestBody)
				.thenReturn(responseBody == null ? null : TestUtils.getAllResourceContent(responseBody))
				.withStatus(responseStatus);
	}
	
	protected void expectValidRequestOk(UnirestMock unirest, String responseBody){
		expectBodyRequestOk(unirest, getValidRequest(), responseBody);
	}
	
	protected void expectValidRequestWithIntegrityOk(UnirestMock unirest, int responseStatus, String responseBody){
		setupIntegrityOk(unirest);
		expectRequest(unirest, getValidRequest(), responseStatus, responseBody);
	}
	
	protected void setupIntegrityWillNeedRefresh(UnirestMock unirest){
		unirest.expect(POST, "https://gql.twitch.tv/integrity")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.thenReturn(TestUtils.getAllResourceContent("api/gql/integrity_needRefresh.json"))
				.withStatus(200);
	}
	
	protected void setupIntegrityNoToken(UnirestMock unirest){
		unirest.expect(POST, "https://gql.twitch.tv/integrity")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.thenReturn(TestUtils.getAllResourceContent("api/gql/integrity_noToken.json"))
				.withStatus(200);
	}
	
	protected void setupIntegrityStatus(UnirestMock unirest, int status){
		unirest.expect(POST, "https://gql.twitch.tv/integrity")
				.header("Authorization", "OAuth " + ACCESS_TOKEN)
				.thenReturn()
				.withStatus(status);
	}
}
