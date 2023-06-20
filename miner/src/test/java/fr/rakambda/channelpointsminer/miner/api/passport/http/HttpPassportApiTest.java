package fr.rakambda.channelpointsminer.miner.api.passport.http;

import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLoginCacher;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.CaptchaSolveRequired;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.InvalidCredentials;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.rakambda.channelpointsminer.miner.api.passport.http.data.LoginResponse;
import fr.rakambda.channelpointsminer.miner.config.login.HttpLoginMethod;
import fr.rakambda.channelpointsminer.miner.event.impl.LoginRequiredEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import kong.unirest.core.Cookie;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static kong.unirest.core.ContentType.APPLICATION_JSON;
import static kong.unirest.core.HeaderNames.CONTENT_TYPE;
import static kong.unirest.core.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
@ParallelizableTest
class HttpPassportApiTest{
	private static final String USER_PASS_REQUEST = "{\"client_id\":\"%s\",\"password\":\"%s\",\"remember_me\":true,\"undelete_user\":false,\"username\":\"%s\"}";
	private static final String USER_PASS_2FA_REQUEST = "{\"authy_token\":\"%s\",\"client_id\":\"%s\",\"password\":\"%s\",\"remember_me\":true,\"undelete_user\":false,\"username\":\"%s\"}";
	private static final String USER_PASS_TWITCHGUARD_REQUEST = "{\"client_id\":\"%s\",\"password\":\"%s\",\"remember_me\":true,\"twitchguard_code\":\"%s\",\"undelete_user\":false,\"username\":\"%s\"}";
	private static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String ACCESS_TOKEN = "access-token";
	private static final String TWO_FACTOR = "123456";
	private static final Instant NOW = Instant.now();
	
	private HttpLoginProvider tested;
	
	@Mock
	private HttpLoginMethod httpLoginMethod;
	@Mock
	private IEventManager eventManager;
	@Mock
	private TwitchLoginCacher cacher;
	
	@BeforeEach
	void setUp(UnirestMock unirestMock) throws IOException{
		lenient().when(httpLoginMethod.getPassword()).thenReturn(PASSWORD);
		lenient().when(httpLoginMethod.isUse2Fa()).thenReturn(false);
		lenient().when(cacher.restoreAuthentication()).thenReturn(Optional.empty());
		
		tested = new HttpLoginProvider(TwitchClient.WEB, unirestMock.getUnirestInstance(), USERNAME, httpLoginMethod, cacher, eventManager);
	}
	
	@Test
	void newAuthWith2FA(UnirestMock unirest) throws LoginException, IOException{
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(httpLoginMethod.isUse2Fa()).thenReturn(true);
			tested = new HttpLoginProvider(TwitchClient.WEB, unirest.getUnirestInstance(), USERNAME, httpLoginMethod, cacher, eventManager);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body(USER_PASS_2FA_REQUEST.formatted(TWO_FACTOR, CLIENT_ID, PASSWORD, USERNAME))
					.thenReturn(LoginResponse.builder().accessToken(ACCESS_TOKEN).build())
					.withHeader("Set-Cookie", "yummy_cookie=choco")
					.withHeader("Set-Cookie", "yummy_cake=vanilla")
					.withStatus(200);
			
			var expected = TwitchLogin.builder()
					.twitchClient(TwitchClient.WEB)
					.username(USERNAME)
					.accessToken(ACCESS_TOKEN)
					.cookies(List.of(
							new Cookie("yummy_cookie=choco"),
							new Cookie("yummy_cake=vanilla")
					))
					.build();
			
			assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
			verify(cacher).saveAuthentication(expected);
			verify(eventManager).onEvent(new LoginRequiredEvent(NOW));
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void newAuthWithout2FA(UnirestMock unirest) throws LoginException, IOException{
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn(LoginResponse.builder().accessToken(ACCESS_TOKEN).build())
				.withHeader("Set-Cookie", "yummy_cookie=choco")
				.withHeader("Set-Cookie", "yummy_cake=vanilla")
				.withStatus(200);
		
		var expected = TwitchLogin.builder()
				.twitchClient(TwitchClient.WEB)
				.username(USERNAME)
				.accessToken(ACCESS_TOKEN)
				.cookies(List.of(
						new Cookie("yummy_cookie=choco"),
						new Cookie("yummy_cake=vanilla")
				))
				.build();
		
		assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
		verify(cacher).saveAuthentication(expected);
		verify(eventManager, never()).onEvent(any());
		
		unirest.verifyAll();
	}
	
	@Test
	void newAuthWithObscuredEmail(UnirestMock unirest) throws LoginException, IOException{
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn(LoginResponse.builder().accessToken(ACCESS_TOKEN).obscuredEmail("t**t@t****.com").build())
				.withHeader("Set-Cookie", "yummy_cookie=choco")
				.withHeader("Set-Cookie", "yummy_cake=vanilla")
				.withStatus(200);
		
		var expected = TwitchLogin.builder()
				.twitchClient(TwitchClient.WEB)
				.username(USERNAME)
				.accessToken(ACCESS_TOKEN)
				.cookies(List.of(
						new Cookie("yummy_cookie=choco"),
						new Cookie("yummy_cake=vanilla")
				))
				.build();
		
		assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
		verify(cacher).saveAuthentication(expected);
		verify(eventManager, never()).onEvent(any());
		
		unirest.verifyAll();
	}
	
	@ParameterizedTest
	@ValueSource(ints = {
			3011,
			3012
	})
	void newAuthWithMissing2FAOnFirstTry(int errorCode, UnirestMock unirest) throws LoginException, IOException{
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
					.thenReturn(LoginResponse.builder().errorCode(errorCode).errorDescription("For tests").build())
					.withStatus(400);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body(USER_PASS_2FA_REQUEST.formatted(TWO_FACTOR, CLIENT_ID, PASSWORD, USERNAME))
					.thenReturn(LoginResponse.builder().accessToken(ACCESS_TOKEN).build())
					.withHeader("Set-Cookie", "yummy_cookie=choco")
					.withHeader("Set-Cookie", "yummy_cake=vanilla")
					.withStatus(200);
			
			var expected = TwitchLogin.builder()
					.twitchClient(TwitchClient.WEB)
					.username(USERNAME)
					.accessToken(ACCESS_TOKEN)
					.cookies(List.of(
							new Cookie("yummy_cookie=choco"),
							new Cookie("yummy_cake=vanilla")
					))
					.build();
			
			assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
			verify(cacher).saveAuthentication(expected);
			verify(eventManager).onEvent(new LoginRequiredEvent(NOW));
			
			unirest.verifyAll();
		}
	}
	
	@ParameterizedTest
	@ValueSource(ints = {
			3022,
			3023
	})
	void newAuthWithMissingTwitchGuardOnFirstTry(int errorCode, UnirestMock unirest) throws LoginException, IOException{
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
					.thenReturn(LoginResponse.builder().errorCode(errorCode).errorDescription("For tests").build())
					.withStatus(400);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body(USER_PASS_TWITCHGUARD_REQUEST.formatted(CLIENT_ID, PASSWORD, TWO_FACTOR, USERNAME))
					.thenReturn(LoginResponse.builder().accessToken(ACCESS_TOKEN).build())
					.withHeader("Set-Cookie", "yummy_cookie=choco")
					.withHeader("Set-Cookie", "yummy_cake=vanilla")
					.withStatus(200);
			
			var expected = TwitchLogin.builder()
					.twitchClient(TwitchClient.WEB)
					.username(USERNAME)
					.accessToken(ACCESS_TOKEN)
					.cookies(List.of(
							new Cookie("yummy_cookie=choco"),
							new Cookie("yummy_cake=vanilla")
					))
					.build();
			
			assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
			verify(cacher).saveAuthentication(expected);
			verify(eventManager).onEvent(new LoginRequiredEvent(NOW));
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void failedAuthWithCaptchaRequired(UnirestMock unirest) throws IOException{
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn(LoginResponse.builder().errorCode(1000).errorDescription("For tests").build())
				.withStatus(400);
		
		assertThrows(CaptchaSolveRequired.class, () -> tested.login());
		verify(cacher, never()).saveAuthentication(any());
		verify(eventManager, never()).onEvent(any());
		
		unirest.verifyAll();
	}
	
	@ParameterizedTest
	@ValueSource(ints = {
			3001,
			3003
	})
	void failedAuthWithInvalidCredentials(int errorCode, UnirestMock unirest) throws IOException{
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn(LoginResponse.builder().errorCode(errorCode).errorDescription("For tests").build())
				.withStatus(400);
		
		assertThrows(InvalidCredentials.class, () -> tested.login());
		verify(cacher, never()).saveAuthentication(any());
		verify(eventManager, never()).onEvent(any());
		
		unirest.verifyAll();
	}
	
	@Test
	void failedAuthWithMissing2FA(UnirestMock unirest) throws IOException{
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
					.thenReturn(LoginResponse.builder().errorCode(3011).errorDescription("For tests").build())
					.withStatus(400);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body(USER_PASS_2FA_REQUEST.formatted(TWO_FACTOR, CLIENT_ID, PASSWORD, USERNAME))
					.thenReturn(LoginResponse.builder().errorCode(3001).errorDescription("For tests").build())
					.withStatus(400);
			
			assertThrows(InvalidCredentials.class, () -> tested.login());
			verify(cacher, never()).saveAuthentication(any());
			verify(eventManager).onEvent(new LoginRequiredEvent(NOW));
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void failedAuthWithMissingTwitchGuard(UnirestMock unirest) throws IOException{
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
					.thenReturn(LoginResponse.builder().errorCode(3022).errorDescription("For tests").build())
					.withStatus(400);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body(USER_PASS_TWITCHGUARD_REQUEST.formatted(CLIENT_ID, PASSWORD, TWO_FACTOR, USERNAME))
					.thenReturn(LoginResponse.builder().errorCode(3001).errorDescription("For tests").build())
					.withStatus(400);
			
			assertThrows(InvalidCredentials.class, () -> tested.login());
			verify(cacher, never()).saveAuthentication(any());
			verify(eventManager).onEvent(new LoginRequiredEvent(NOW));
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void failedAuthWithUnknownErrorCode(UnirestMock unirest) throws IOException{
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn(LoginResponse.builder().errorCode(9999).errorDescription("For tests").build())
				.withStatus(400);
		
		assertThrows(LoginException.class, () -> tested.login());
		verify(cacher, never()).saveAuthentication(any());
		verify(eventManager, never()).onEvent(any());
		
		unirest.verifyAll();
	}
	
	@Test
	void failedAuthWithNoErrorCode(UnirestMock unirest) throws IOException{
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn(LoginResponse.builder().errorDescription("For tests").build())
				.withStatus(400);
		
		assertThrows(LoginException.class, () -> tested.login());
		verify(cacher, never()).saveAuthentication(any());
		verify(eventManager, never()).onEvent(any());
		
		unirest.verifyAll();
	}
	
	@Test
	void failedAuthWithServerError(UnirestMock unirest) throws IOException{
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn()
				.withStatus(500);
		
		assertThrows(LoginException.class, () -> tested.login());
		verify(cacher, never()).saveAuthentication(any());
		verify(eventManager, never()).onEvent(any());
		
		unirest.verifyAll();
	}
	
	@Test
	void restoreAuth() throws LoginException, IOException{
		var login = TwitchLogin.builder()
				.twitchClient(TwitchClient.WEB)
				.username(USERNAME)
				.accessToken(ACCESS_TOKEN)
				.cookies(List.of(
						new Cookie("yummy_cookie=choco"),
						new Cookie("yummy_cake=vanilla")
				))
				.build();
		
		when(cacher.restoreAuthentication()).thenReturn(Optional.of(login));
		assertThat(tested.login()).usingRecursiveComparison().isEqualTo(login);
		verify(cacher, never()).saveAuthentication(any());
		verify(eventManager, never()).onEvent(any());
	}
	
	@Test
	void restoreAuthError() throws IOException{
		when(cacher.restoreAuthentication()).thenThrow(IOException.class);
		assertThrows(IOException.class, () -> tested.login());
		verify(eventManager, never()).onEvent(any());
	}
	
	@Test
	void restoreWrongClient() throws IOException{
		var login = TwitchLogin.builder()
				.twitchClient(TwitchClient.MOBILE)
				.username(USERNAME)
				.accessToken(ACCESS_TOKEN)
				.cookies(List.of(
						new Cookie("yummy_cookie=choco"),
						new Cookie("yummy_cake=vanilla")
				))
				.build();
		
		when(cacher.restoreAuthentication()).thenReturn(Optional.of(login));
		
		assertThrows(LoginException.class, () -> tested.login());
		verify(eventManager, never()).onEvent(any());
	}
}