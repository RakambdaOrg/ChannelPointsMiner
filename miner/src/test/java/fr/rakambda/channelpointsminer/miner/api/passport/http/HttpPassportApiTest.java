package fr.rakambda.channelpointsminer.miner.api.passport.http;

import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.CaptchaSolveRequired;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.InvalidCredentials;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.rakambda.channelpointsminer.miner.api.passport.http.data.LoginResponse;
import fr.rakambda.channelpointsminer.miner.config.login.HttpLoginMethod;
import fr.rakambda.channelpointsminer.miner.tests.TestUtils;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMock;
import fr.rakambda.channelpointsminer.miner.tests.UnirestMockExtension;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import kong.unirest.core.Cookie;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static kong.unirest.core.ContentType.APPLICATION_JSON;
import static kong.unirest.core.HeaderNames.CONTENT_TYPE;
import static kong.unirest.core.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(UnirestMockExtension.class)
class HttpPassportApiTest{
	private static final String USER_PASS_REQUEST = "{\"client_id\":\"%s\",\"password\":\"%s\",\"remember_me\":true,\"undelete_user\":false,\"username\":\"%s\"}";
	private static final String USER_PASS_2FA_REQUEST = "{\"authy_token\":\"%s\",\"client_id\":\"%s\",\"password\":\"%s\",\"remember_me\":true,\"undelete_user\":false,\"username\":\"%s\"}";
	private static final String USER_PASS_TWITCHGUARD_REQUEST = "{\"client_id\":\"%s\",\"password\":\"%s\",\"remember_me\":true,\"twitchguard_code\":\"%s\",\"undelete_user\":false,\"username\":\"%s\"}";
	private static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String USER_ID = "user-id";
	private static final String ACCESS_TOKEN = "access-token";
	private static final String TWO_FACTOR = "123456";
	
	@TempDir
	private Path authFolder;
	
	private HttpPassportApi tested;
	
	@Mock
	private HttpLoginMethod httpLoginMethod;
	
	private Path authFile;
	
	@BeforeEach
	void setUp(UnirestMock unirestMock){
		authFile = authFolder.resolve(USERNAME + ".json");
		
		lenient().when(httpLoginMethod.getPassword()).thenReturn(PASSWORD);
		lenient().when(httpLoginMethod.getAuthenticationFolder()).thenReturn(authFolder);
		lenient().when(httpLoginMethod.isUse2Fa()).thenReturn(false);
		
		tested = new HttpPassportApi(TwitchClient.WEB, unirestMock.getUnirestInstance(), USERNAME, httpLoginMethod);
	}
	
	@Test
	void newAuthWith2FA(UnirestMock unirest) throws LoginException, IOException{
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
			
			when(httpLoginMethod.isUse2Fa()).thenReturn(true);
			tested = new HttpPassportApi(TwitchClient.WEB, unirest.getUnirestInstance(), USERNAME, httpLoginMethod);
			
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
			assertThat(authFile).exists().isNotEmptyFile();
			JsonAssertions.assertThatJson(TestUtils.getAllContent(authFile)).isEqualTo(TestUtils.getAllResourceContent("api/passport/expectedAuth.json"));
			
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
		assertThat(authFile).exists().isNotEmptyFile();
		JsonAssertions.assertThatJson(TestUtils.getAllContent(authFile)).isEqualTo(TestUtils.getAllResourceContent("api/passport/expectedAuth.json"));
		
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
		assertThat(authFile).exists().isNotEmptyFile();
		JsonAssertions.assertThatJson(TestUtils.getAllContent(authFile)).isEqualTo(TestUtils.getAllResourceContent("api/passport/expectedAuth.json"));
		
		unirest.verifyAll();
	}
	
	@ParameterizedTest
	@ValueSource(ints = {
			3011,
			3012
	})
	void newAuthWithMissing2FAOnFirstTry(int errorCode, UnirestMock unirest) throws LoginException, IOException{
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
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
			assertThat(authFile).exists().isNotEmptyFile();
			JsonAssertions.assertThatJson(TestUtils.getAllContent(authFile)).isEqualTo(TestUtils.getAllResourceContent("api/passport/expectedAuth.json"));
			
			unirest.verifyAll();
		}
	}
	
	@ParameterizedTest
	@ValueSource(ints = {
			3022,
			3023
	})
	void newAuthWithMissingTwitchGuardOnFirstTry(int errorCode, UnirestMock unirest) throws LoginException, IOException{
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
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
			assertThat(authFile).exists().isNotEmptyFile();
			JsonAssertions.assertThatJson(TestUtils.getAllContent(authFile)).isEqualTo(TestUtils.getAllResourceContent("api/passport/expectedAuth.json"));
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void failedAuthWithCaptchaRequired(UnirestMock unirest){
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn(LoginResponse.builder().errorCode(1000).errorDescription("For tests").build())
				.withStatus(400);
		
		assertThrows(CaptchaSolveRequired.class, () -> tested.login());
		assertThat(authFile).doesNotExist();
		
		unirest.verifyAll();
	}
	
	@ParameterizedTest
	@ValueSource(ints = {
			3001,
			3003
	})
	void failedAuthWithInvalidCredentials(int errorCode, UnirestMock unirest){
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn(LoginResponse.builder().errorCode(errorCode).errorDescription("For tests").build())
				.withStatus(400);
		
		assertThrows(InvalidCredentials.class, () -> tested.login());
		assertThat(authFile).doesNotExist();
		
		unirest.verifyAll();
	}
	
	@Test
	void failedAuthWithMissing2FA(UnirestMock unirest){
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
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
			assertThat(authFile).doesNotExist();
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void failedAuthWithMissingTwitchGuard(UnirestMock unirest){
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
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
			assertThat(authFile).doesNotExist();
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void failedAuthWithUnknownErrorCode(UnirestMock unirest){
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn(LoginResponse.builder().errorCode(9999).errorDescription("For tests").build())
				.withStatus(400);
		
		assertThrows(LoginException.class, () -> tested.login());
		assertThat(authFile).doesNotExist();
		
		unirest.verifyAll();
	}
	
	@Test
	void failedAuthWithNoErrorCode(UnirestMock unirest){
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn(LoginResponse.builder().errorDescription("For tests").build())
				.withStatus(400);
		
		assertThrows(LoginException.class, () -> tested.login());
		assertThat(authFile).doesNotExist();
		
		unirest.verifyAll();
	}
	
	@Test
	void failedAuthWithServerError(UnirestMock unirest){
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(USER_PASS_REQUEST.formatted(CLIENT_ID, PASSWORD, USERNAME))
				.thenReturn()
				.withStatus(500);
		
		assertThrows(LoginException.class, () -> tested.login());
		assertThat(authFile).doesNotExist();
		
		unirest.verifyAll();
	}
	
	@Test
	void restoreAuth() throws LoginException, IOException{
		TestUtils.copyFromResources("api/passport/expectedAuth.json", authFile);
		
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
		assertThat(authFile).exists();
	}
	
	@Test
	void restoreAuthWithUserId() throws LoginException, IOException{
		TestUtils.copyFromResources("api/passport/expectedAuthWithClientId.json", authFile);
		
		var expected = TwitchLogin.builder()
				.twitchClient(TwitchClient.WEB)
				.username(USERNAME)
				.accessToken(ACCESS_TOKEN)
				.cookies(List.of(
						new Cookie("yummy_cookie=choco"),
						new Cookie("yummy_cake=vanilla")
				))
				.userId(USER_ID)
				.build();
		
		assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
		assertThat(authFile).exists();
	}
	
	@Test
	void restoreAuthBadFile(){
		TestUtils.copyFromResources("api/passport/badAuthFile.json", authFile);
		
		assertThrows(IOException.class, () -> tested.login());
		assertThat(authFile).exists();
	}
	
	@Test
	void restoreWrongClient(){
		TestUtils.copyFromResources("api/passport/mobileAuth.json", authFile);
		
		assertThrows(LoginException.class, () -> tested.login());
		assertThat(authFile).exists();
	}
}