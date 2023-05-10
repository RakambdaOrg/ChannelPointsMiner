package fr.rakambda.channelpointsminer.miner.api.gql.integrity.browser;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityData;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityResponse;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.rakambda.channelpointsminer.miner.browser.Browser;
import fr.rakambda.channelpointsminer.miner.browser.BrowserController;
import fr.rakambda.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.rakambda.channelpointsminer.miner.factory.BrowserFactory;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.devtools.v113.network.model.Headers;
import org.openqa.selenium.devtools.v113.network.model.MonotonicTime;
import org.openqa.selenium.devtools.v113.network.model.Request;
import org.openqa.selenium.devtools.v113.network.model.RequestId;
import org.openqa.selenium.devtools.v113.network.model.RequestWillBeSent;
import org.openqa.selenium.devtools.v113.network.model.Response;
import org.openqa.selenium.devtools.v113.network.model.ResponseReceived;
import org.openqa.selenium.devtools.v113.page.model.FrameId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class BrowserIntegrityProviderTest{
	private static final String CLIENT_SESSION_ID = "client-session-id";
	private static final String CLIENT_VERSION = "client-version";
	private static final String TOKEN = "token";
	private static final Instant EXPIRATION = Instant.parse("2022-02-02T02:02:02.000Z");
	private static final String X_DEVICE_ID = "x-device-id";
	private static final String RESPONSE_BODY = "request-body";
	private static final IntegrityResponse INTEGRITY_RESPONSE = IntegrityResponse.builder()
			.token(TOKEN)
			.expiration(EXPIRATION)
			.build();
	
	@InjectMocks
	private BrowserIntegrityProvider tested;
	
	@Mock
	private BrowserConfiguration browserConfiguration;
	@Mock
	private Browser browser;
	@Mock
	private BrowserController browserController;
	@Mock
	private ResponseReceived receivedResponse;
	@Mock
	private Response response;
	@Mock
	private RequestWillBeSent requestWillBeSent;
	@Mock
	private Request request;
	@Mock
	private RequestId requestId;
	@Mock
	private Headers headers;
	
	@BeforeEach
	void setUp(){
		lenient().when(browser.setup()).thenReturn(browserController);
		lenient().when(browser.getRequestBody(requestId)).thenReturn(RESPONSE_BODY);
		
		var frameId = "frame-id";
		lenient().when(browser.getReceivedResponses()).thenReturn(List.of(receivedResponse));
		lenient().when(receivedResponse.getTimestamp()).thenReturn(new MonotonicTime(1));
		lenient().when(receivedResponse.getFrameId()).thenReturn(Optional.of(new FrameId(frameId)));
		lenient().when(receivedResponse.getRequestId()).thenReturn(requestId);
		lenient().when(receivedResponse.getResponse()).thenReturn(response);
		lenient().when(response.getUrl()).thenReturn("https://gql.twitch.tv/integrity");
		
		lenient().when(browser.getSentRequests()).thenReturn(List.of(requestWillBeSent));
		lenient().when(requestWillBeSent.getFrameId()).thenReturn(Optional.of(new FrameId(frameId)));
		lenient().when(requestWillBeSent.getRequest()).thenReturn(request);
		lenient().when(request.getUrl()).thenReturn("https://gql.twitch.tv/integrity");
		lenient().when(request.getHeaders()).thenReturn(headers);
		lenient().when(headers.get("Client-Session-Id")).thenReturn(CLIENT_SESSION_ID);
		lenient().when(headers.get("Client-Version")).thenReturn(CLIENT_VERSION);
		lenient().when(headers.get("X-Device-Id")).thenReturn(X_DEVICE_ID);
	}
	
	@Test
	void integrityIsRetrieved() throws IntegrityException{
		try(var browserFactory = mockStatic(BrowserFactory.class);
				var jacksonUtils = mockStatic(JacksonUtils.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			jacksonUtils.when(() -> JacksonUtils.read(eq(RESPONSE_BODY), any(TypeReference.class))).thenReturn(INTEGRITY_RESPONSE);
			
			assertThat(tested.getIntegrity()).contains(IntegrityData.builder()
					.clientSessionId(CLIENT_SESSION_ID)
					.clientVersion(CLIENT_VERSION)
					.token(TOKEN)
					.expiration(EXPIRATION)
					.xDeviceId(X_DEVICE_ID)
					.build());
		}
	}
	
	//TODO check integrity is reused if not expired
	
	@Test
	void invalidate(){
		//TODO improve this
		tested.invalidate();
	}
	
	@Test
	void browserError(){
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			when(browser.setup()).thenThrow(new RuntimeException("For tests"));
			
			assertThrows(RuntimeException.class, tested::getIntegrity);
		}
	}
	
	@Test
	void notLoggedIn() throws LoginException{
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			doThrow(new LoginException("For tests")).when(browserController).ensureLoggedIn();
			
			assertThrows(IntegrityException.class, tested::getIntegrity);
		}
	}
	
	@Test
	void noIntegrityResponse(){
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			when(response.getUrl()).thenReturn("https://nope");
			
			assertThrows(IntegrityException.class, tested::getIntegrity);
		}
	}
	
	@Test
	void noResponseFrameId(){
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			when(receivedResponse.getFrameId()).thenReturn(Optional.empty());
			
			assertThrows(IntegrityException.class, tested::getIntegrity);
		}
	}
	
	@Test
	void noIntegrityRequest(){
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			when(request.getUrl()).thenReturn("https://nope");
			
			assertThrows(IntegrityException.class, tested::getIntegrity);
		}
	}
	
	@Test
	void noRequestFrameId(){
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			when(requestWillBeSent.getFrameId()).thenReturn(Optional.empty());
			
			assertThrows(IntegrityException.class, tested::getIntegrity);
		}
	}
	
	@Test
	void notMatchingFrameId(){
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			when(requestWillBeSent.getFrameId()).thenReturn(Optional.of(new FrameId("other")));
			
			assertThrows(IntegrityException.class, tested::getIntegrity);
		}
	}
	
	@Test
	void integrityResponseReadException(){
		try(var browserFactory = mockStatic(BrowserFactory.class);
				var jacksonUtils = mockStatic(JacksonUtils.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			jacksonUtils.when(() -> JacksonUtils.read(anyString(), any(TypeReference.class))).thenThrow(new IOException("For tests"));
			
			assertThrows(IntegrityException.class, tested::getIntegrity);
		}
	}
	
	@Test
	void noClientSessionIdCookie(){
		try(var browserFactory = mockStatic(BrowserFactory.class);
				var jacksonUtils = mockStatic(JacksonUtils.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			jacksonUtils.when(() -> JacksonUtils.read(eq(RESPONSE_BODY), any(TypeReference.class))).thenReturn(INTEGRITY_RESPONSE);
			
			lenient().when(headers.get("Client-Session-Id")).thenReturn(null);
			
			assertThrows(IntegrityException.class, tested::getIntegrity);
		}
	}
	
	@Test
	void noClientVersionCookie(){
		try(var browserFactory = mockStatic(BrowserFactory.class);
				var jacksonUtils = mockStatic(JacksonUtils.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			jacksonUtils.when(() -> JacksonUtils.read(eq(RESPONSE_BODY), any(TypeReference.class))).thenReturn(INTEGRITY_RESPONSE);
			
			lenient().when(headers.get("Client-Version")).thenReturn(null);
			
			assertThrows(IntegrityException.class, tested::getIntegrity);
		}
	}
	
	@Test
	void noXDeviceIdCookie(){
		try(var browserFactory = mockStatic(BrowserFactory.class);
				var jacksonUtils = mockStatic(JacksonUtils.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			jacksonUtils.when(() -> JacksonUtils.read(eq(RESPONSE_BODY), any(TypeReference.class))).thenReturn(INTEGRITY_RESPONSE);
			
			lenient().when(headers.get("X-Device-Id")).thenReturn(null);
			
			assertThrows(IntegrityException.class, tested::getIntegrity);
		}
	}
}