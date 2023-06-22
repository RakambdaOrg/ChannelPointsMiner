package fr.rakambda.channelpointsminer.miner.api.passport.oauth;

import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLoginCacher;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.rakambda.channelpointsminer.miner.api.passport.oauth.data.DeviceResponse;
import fr.rakambda.channelpointsminer.miner.api.passport.oauth.data.TokenResponse;
import fr.rakambda.channelpointsminer.miner.event.impl.LoginRequiredEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import kong.unirest.core.FieldMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static kong.unirest.core.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
@ParallelizableTest
class OauthLoginProviderTest{
	private static final String CLIENT_ID = "ue6666qo983tsx6so1t0vnawi233wa";
	private static final String USERNAME = "username";
	private static final String ACCESS_TOKEN = "access-token";
	private static final String DEVICE_CODE = "deviceCode";
	private static final String USER_CODE = "userCode";
	private static final Instant NOW = Instant.parse("2022-01-05T10:52:32.000Z");
	
	private OauthLoginProvider tested;
	
	@Mock
	private TwitchLoginCacher cacher;
	@Mock
	private IEventManager eventManager;
	
	@BeforeEach
	void setUp(UnirestMock unirestMock) throws IOException{
		lenient().when(cacher.restoreAuthentication()).thenReturn(Optional.empty());
		
		tested = new OauthLoginProvider(TwitchClient.ANDROID_TV, unirestMock.getUnirestInstance(), USERNAME, cacher, eventManager);
	}
	
	@Test
	void newAuth(UnirestMock unirest) throws LoginException, IOException{
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			setupOkDeviceRequest(unirest);
			setupOkTokenRequest(unirest);
			
			var expected = TwitchLogin.builder()
					.twitchClient(TwitchClient.ANDROID_TV)
					.username(USERNAME)
					.accessToken(ACCESS_TOKEN)
					.cookies(List.of())
					.build();
			
			assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
			verify(cacher).saveAuthentication(expected);
			verify(eventManager).onEvent(new LoginRequiredEvent(NOW));
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void newAuthWithRetry(UnirestMock unirest) throws LoginException, IOException{
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			setupOkDeviceRequest(unirest);
			unirest.expect(POST, "https://id.twitch.tv/oauth2/token")
					.body(new FieldMatcher(Map.of(
							"client_id", CLIENT_ID,
							"grant_type", "urn:ietf:params:oauth:grant-type:device_code",
							"device_code", DEVICE_CODE
					)))
					.thenReturn(TokenResponse.builder()
							.status("400")
							.message("authorization_pending")
							.build())
					.withStatus(400)
					.thenReturn(TokenResponse.builder()
							.accessToken(ACCESS_TOKEN)
							.build())
					.withStatus(200);
			
			var expected = TwitchLogin.builder()
					.twitchClient(TwitchClient.ANDROID_TV)
					.username(USERNAME)
					.accessToken(ACCESS_TOKEN)
					.cookies(List.of())
					.build();
			
			assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
			verify(cacher).saveAuthentication(expected);
			verify(eventManager).onEvent(new LoginRequiredEvent(NOW));
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void failureToGetDeviceCode(UnirestMock unirest) throws IOException{
		setupDeviceRequest(unirest, 400, DeviceResponse.builder().status("400").message("For test").build());
		
		assertThrows(LoginException.class, () -> tested.login());
		verify(cacher, never()).saveAuthentication(any());
		verify(eventManager, never()).onEvent(any(LoginRequiredEvent.class));
		
		unirest.verifyAll();
	}
	
	@Test
	void failureToGetToken(UnirestMock unirest) throws IOException{
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			setupOkDeviceRequest(unirest);
			setupTokenRequest(unirest, 400, TokenResponse.builder().status("400").message("For test").build());
			
			assertThrows(LoginException.class, () -> tested.login());
			verify(cacher, never()).saveAuthentication(any());
			verify(eventManager).onEvent(new LoginRequiredEvent(NOW));
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void deviceCodeNeverEntered(UnirestMock unirest) throws IOException{
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			setupOkDeviceRequest(unirest);
			setupTokenRequest(unirest, 400, TokenResponse.builder().status("400").message("authorization_pending").build());
			
			assertThrows(LoginException.class, () -> tested.login());
			verify(cacher, never()).saveAuthentication(any());
			verify(eventManager).onEvent(new LoginRequiredEvent(NOW));
			
			unirest.verifyAll();
		}
	}
	
	private void setupOkDeviceRequest(UnirestMock unirest){
		setupDeviceRequest(unirest, 200, DeviceResponse.builder()
				.deviceCode(DEVICE_CODE)
				.expiresIn(5)
				.interval(1)
				.userCode(USER_CODE)
				.verificationUri("https://verification/")
				.build());
	}
	
	private void setupDeviceRequest(UnirestMock unirest, int status, DeviceResponse response){
		unirest.expect(POST, "https://id.twitch.tv/oauth2/device")
				.body(new FieldMatcher(Map.of(
						"client_id", CLIENT_ID,
						"scopes", "channel_read chat:read user_blocks_edit user_blocks_read user_follows_edit user_read"
				)))
				.thenReturn(response)
				.withStatus(status);
	}
	
	private void setupOkTokenRequest(UnirestMock unirest){
		setupTokenRequest(unirest, 200, TokenResponse.builder()
				.accessToken(ACCESS_TOKEN)
				.build());
	}
	
	private void setupTokenRequest(UnirestMock unirest, int status, TokenResponse response){
		unirest.expect(POST, "https://id.twitch.tv/oauth2/token")
				.body(new FieldMatcher(Map.of(
						"client_id", CLIENT_ID,
						"grant_type", "urn:ietf:params:oauth:grant-type:device_code",
						"device_code", DEVICE_CODE
				)))
				.thenReturn(response)
				.withStatus(status);
	}
	
	@Test
	void restoreAuth() throws LoginException, IOException{
		var login = TwitchLogin.builder()
				.twitchClient(TwitchClient.ANDROID_TV)
				.username(USERNAME)
				.accessToken(ACCESS_TOKEN)
				.cookies(List.of())
				.build();
		
		when(cacher.restoreAuthentication()).thenReturn(Optional.of(login));
		assertThat(tested.login()).usingRecursiveComparison().isEqualTo(login);
		verify(cacher, never()).saveAuthentication(any());
		verify(eventManager, never()).onEvent(any(LoginRequiredEvent.class));
	}
	
	@Test
	void restoreAuthError() throws IOException{
		when(cacher.restoreAuthentication()).thenThrow(IOException.class);
		assertThrows(IOException.class, () -> tested.login());
		verify(eventManager, never()).onEvent(any(LoginRequiredEvent.class));
	}
	
	@Test
	void restoreWrongClient() throws IOException{
		var login = TwitchLogin.builder()
				.twitchClient(TwitchClient.MOBILE)
				.username(USERNAME)
				.accessToken(ACCESS_TOKEN)
				.cookies(List.of())
				.build();
		
		when(cacher.restoreAuthentication()).thenReturn(Optional.of(login));
		
		assertThrows(LoginException.class, () -> tested.login());
		verify(eventManager, never()).onEvent(any(LoginRequiredEvent.class));
	}
}