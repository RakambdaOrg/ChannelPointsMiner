package fr.raksrinana.twitchminer.api.passport;

import fr.raksrinana.twitchminer.TestUtils;
import fr.raksrinana.twitchminer.api.passport.data.LoginResponse;
import fr.raksrinana.twitchminer.api.passport.exceptions.CaptchaSolveRequired;
import fr.raksrinana.twitchminer.api.passport.exceptions.InvalidCredentials;
import fr.raksrinana.twitchminer.api.passport.exceptions.LoginException;
import fr.raksrinana.twitchminer.utils.CommonUtils;
import kong.unirest.Cookie;
import kong.unirest.MockClient;
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
import static fr.raksrinana.twitchminer.TestUtils.copyFromResources;
import static fr.raksrinana.twitchminer.TestUtils.getResourcePath;
import static kong.unirest.ContentType.APPLICATION_JSON;
import static kong.unirest.HeaderNames.CONTENT_TYPE;
import static kong.unirest.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class PassportApiTest{
	private static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String USER_ID = "user-id";
	private static final String ACCESS_TOKEN = "access-token";
	private static final String TWO_FACTOR = "123456";
	
	@TempDir
	private Path authFolder;
	
	private PassportApi tested;
	
	private Path authFile;
	private MockClient unirest;
	
	@BeforeEach
	void setUp(){
		TestUtils.setupUnirest();
		unirest = MockClient.register();
		
		tested = new PassportApi(USERNAME, PASSWORD, authFolder, false);
		
		authFile = authFolder.resolve(USERNAME + ".json");
	}
	
	@Test
	void newAuthWithout2FA() throws LoginException, IOException{
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD))
				.thenReturn(LoginResponse.builder().accessToken(ACCESS_TOKEN).build())
				.withHeader("Set-Cookie", "yummy_cookie=choco")
				.withHeader("Set-Cookie", "yummy_cake=vanilla")
				.withStatus(200);
		
		var expected = TwitchLogin.builder()
				.username(USERNAME)
				.accessToken(ACCESS_TOKEN)
				.cookies(List.of(
						new Cookie("yummy_cookie=choco"),
						new Cookie("yummy_cake=vanilla")
				))
				.build();
		
		assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
		assertThat(authFile).exists().hasSameTextualContentAs(getResourcePath("api/passport/expectedAuth.json"));
		
		unirest.verifyAll();
	}
	
	@Test
	void newAuthWith2FA() throws LoginException, IOException{
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
			
			tested = new PassportApi(USERNAME, PASSWORD, authFolder, true);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\",\"authy_token\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD, TWO_FACTOR))
					.thenReturn(LoginResponse.builder().accessToken(ACCESS_TOKEN).build())
					.withHeader("Set-Cookie", "yummy_cookie=choco")
					.withHeader("Set-Cookie", "yummy_cake=vanilla")
					.withStatus(200);
			
			var expected = TwitchLogin.builder()
					.username(USERNAME)
					.accessToken(ACCESS_TOKEN)
					.cookies(List.of(
							new Cookie("yummy_cookie=choco"),
							new Cookie("yummy_cake=vanilla")
					))
					.build();
			
			assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
			assertThat(authFile).exists().hasSameTextualContentAs(getResourcePath("api/passport/expectedAuth.json"));
			
			unirest.verifyAll();
		}
	}
	
	@ParameterizedTest
	@ValueSource(ints = {
			3011,
			3012
	})
	void newAuthWithMissing2FAOnFirstTry(int errorCode) throws LoginException, IOException{
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD))
					.thenReturn(LoginResponse.builder().errorCode(errorCode).errorDescription("For tests").build())
					.withStatus(400);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\",\"authy_token\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD, TWO_FACTOR))
					.thenReturn(LoginResponse.builder().accessToken(ACCESS_TOKEN).build())
					.withHeader("Set-Cookie", "yummy_cookie=choco")
					.withHeader("Set-Cookie", "yummy_cake=vanilla")
					.withStatus(200);
			
			var expected = TwitchLogin.builder()
					.username(USERNAME)
					.accessToken(ACCESS_TOKEN)
					.cookies(List.of(
							new Cookie("yummy_cookie=choco"),
							new Cookie("yummy_cake=vanilla")
					))
					.build();
			
			assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
			assertThat(authFile).exists().hasSameTextualContentAs(getResourcePath("api/passport/expectedAuth.json"));
			
			unirest.verifyAll();
		}
	}
	
	@ParameterizedTest
	@ValueSource(ints = {
			3022,
			3023
	})
	void newAuthWithMissingTwitchGuardOnFirstTry(int errorCode) throws LoginException, IOException{
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD))
					.thenReturn(LoginResponse.builder().errorCode(errorCode).errorDescription("For tests").build())
					.withStatus(400);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\",\"twitchguard_code\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD, TWO_FACTOR))
					.thenReturn(LoginResponse.builder().accessToken(ACCESS_TOKEN).build())
					.withHeader("Set-Cookie", "yummy_cookie=choco")
					.withHeader("Set-Cookie", "yummy_cake=vanilla")
					.withStatus(200);
			
			var expected = TwitchLogin.builder()
					.username(USERNAME)
					.accessToken(ACCESS_TOKEN)
					.cookies(List.of(
							new Cookie("yummy_cookie=choco"),
							new Cookie("yummy_cake=vanilla")
					))
					.build();
			
			assertThat(tested.login()).usingRecursiveComparison().isEqualTo(expected);
			assertThat(authFile).exists().hasSameTextualContentAs(getResourcePath("api/passport/expectedAuth.json"));
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void failedAuthWithCaptchaRequired(){
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD))
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
	void failedAuthWithInvalidCredentials(int errorCode){
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD))
				.thenReturn(LoginResponse.builder().errorCode(errorCode).errorDescription("For tests").build())
				.withStatus(400);
		
		assertThrows(InvalidCredentials.class, () -> tested.login());
		assertThat(authFile).doesNotExist();
		
		unirest.verifyAll();
	}
	
	@Test
	void failedAuthWithMissing2FA(){
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD))
					.thenReturn(LoginResponse.builder().errorCode(3011).errorDescription("For tests").build())
					.withStatus(400);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\",\"authy_token\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD, TWO_FACTOR))
					.thenReturn(LoginResponse.builder().errorCode(3001).errorDescription("For tests").build())
					.withStatus(400);
			
			assertThrows(InvalidCredentials.class, () -> tested.login());
			assertThat(authFile).doesNotExist();
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void failedAuthWithMissingTwitchGuard(){
		try(var commonUtils = Mockito.mockStatic(CommonUtils.class)){
			commonUtils.when(() -> CommonUtils.getUserInput(anyString())).thenReturn(TWO_FACTOR);
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD))
					.thenReturn(LoginResponse.builder().errorCode(3022).errorDescription("For tests").build())
					.withStatus(400);
			
			unirest.expect(POST, "https://passport.twitch.tv/login")
					.header(CONTENT_TYPE, APPLICATION_JSON.toString())
					.header("Client-Id", CLIENT_ID)
					.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\",\"twitchguard_code\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD, TWO_FACTOR))
					.thenReturn(LoginResponse.builder().errorCode(3001).errorDescription("For tests").build())
					.withStatus(400);
			
			assertThrows(InvalidCredentials.class, () -> tested.login());
			assertThat(authFile).doesNotExist();
			
			unirest.verifyAll();
		}
	}
	
	@Test
	void failedAuthWithUnknownErrorCode(){
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD))
				.thenReturn(LoginResponse.builder().errorCode(9999).errorDescription("For tests").build())
				.withStatus(400);
		
		assertThrows(LoginException.class, () -> tested.login());
		assertThat(authFile).doesNotExist();
		
		unirest.verifyAll();
	}
	
	@Test
	void failedAuthWithNoErrorCode(){
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD))
				.thenReturn(LoginResponse.builder().errorDescription("For tests").build())
				.withStatus(400);
		
		assertThrows(LoginException.class, () -> tested.login());
		assertThat(authFile).doesNotExist();
		
		unirest.verifyAll();
	}
	
	@Test
	void failedAuthWithServerError(){
		unirest.expect(POST, "https://passport.twitch.tv/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body("{\"client_id\":\"%s\",\"undelete_user\":false,\"remember_me\":true,\"username\":\"%s\",\"password\":\"%s\"}".formatted(CLIENT_ID, USERNAME, PASSWORD))
				.thenReturn()
				.withStatus(500);
		
		assertThrows(LoginException.class, () -> tested.login());
		assertThat(authFile).doesNotExist();
		
		unirest.verifyAll();
	}
	
	@Test
	void restoreAuth() throws LoginException, IOException{
		copyFromResources("api/passport/expectedAuth.json", authFile);
		
		var expected = TwitchLogin.builder()
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
		copyFromResources("api/passport/expectedAuthWithClientId.json", authFile);
		
		var expected = TwitchLogin.builder()
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
		copyFromResources("api/passport/badAuthFile.json", authFile);
		
		assertThrows(IOException.class, () -> tested.login());
		assertThat(authFile).exists();
	}
	

}