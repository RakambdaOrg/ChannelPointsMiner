package fr.raksrinana.twitchminer.api.passport;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.raksrinana.twitchminer.api.passport.data.LoginRequest;
import fr.raksrinana.twitchminer.api.passport.data.LoginResponse;
import fr.raksrinana.twitchminer.api.passport.exceptions.*;
import fr.raksrinana.twitchminer.utils.json.JacksonUtils;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.twitchminer.utils.CommonUtils.getUserInput;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static kong.unirest.ContentType.APPLICATION_JSON;
import static kong.unirest.HeaderNames.CONTENT_TYPE;

@Log4j2
public class PassportApi{
	private static final String ENDPOINT = "https://passport.twitch.tv";
	public static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
	
	private final String username;
	private final String password;
	private final boolean ask2FA;
	private final Path userAuthenticationFile;
	
	/**
	 * @param username             Username of the user.
	 * @param password             Password of the user.
	 * @param authenticationFolder File containing authentication to restore.
	 */
	public PassportApi(@NotNull String username, @NotNull String password, @NotNull Path authenticationFolder, boolean ask2FA){
		this.username = username;
		this.password = password;
		this.ask2FA = ask2FA;
		
		userAuthenticationFile = authenticationFolder.resolve(username + ".json");
	}
	
	/**
	 * Attempts a login towards Twitch. If a previous authentication file exists, it'll be restored. Else a login will be performed.
	 *
	 * @return {@link TwitchLogin}.
	 *
	 * @throws IOException    Authentication file errors.
	 * @throws LoginException Login request failed.
	 */
	@NotNull
	public TwitchLogin login() throws LoginException, IOException{
		var restoredAuth = restoreAuthentication();
		if(restoredAuth.isPresent()){
			log.info("Logged back in from authentication file");
			return restoredAuth.get();
		}
		
		HttpResponse<LoginResponse> response;
		try{
			if(ask2FA){
				response = twoFactorLogin();
			}
			else{
				response = login(LoginRequest.builder().username(username).password(password).build());
			}
		}
		catch(MissingAuthy2FA e){
			response = twoFactorLogin();
		}
		catch(MissingTwitchGuard e){
			response = login(LoginRequest.builder().username(username).password(password).twitchGuardCode(getUserInput("Enter TwitchGuard code:")).build());
		}
		
		log.info("Logged in");
		return handleResponse(response);
	}
	
	/**
	 * Login with username, password and 2FA.
	 *
	 * @return Response received.
	 *
	 * @throws LoginException Login request failed.
	 */
	@NotNull
	private HttpResponse<LoginResponse> twoFactorLogin() throws LoginException{
		return login(LoginRequest.builder().username(username).password(password).authyToken(getUserInput("Enter 2FA token:")).build());
	}
	
	/**
	 * @param response Response.
	 *
	 * @return {@link TwitchLogin}.
	 *
	 * @throws IOException File failed to write.
	 */
	@NotNull
	private TwitchLogin handleResponse(@NotNull HttpResponse<LoginResponse> response) throws IOException{
		var twitchLogin = TwitchLogin.builder()
				.username(username)
				.accessToken(response.getBody().getAccessToken())
				.cookies(response.getCookies())
				.build();
		saveAuthentication(twitchLogin);
		return twitchLogin;
	}
	
	/**
	 * Save authentication received from response into a file.
	 *
	 * @param twitchLogin Authentication to save.
	 *
	 * @throws IOException File failed to write.
	 */
	private void saveAuthentication(@NotNull TwitchLogin twitchLogin) throws IOException{
		Files.createDirectories(userAuthenticationFile.getParent());
		JacksonUtils.write(Files.newOutputStream(userAuthenticationFile, CREATE, TRUNCATE_EXISTING), twitchLogin);
	}
	
	/**
	 * Restore authentication from a file.
	 *
	 * @return {@link TwitchLogin} if authentication was restored, empty otherwise.
	 *
	 * @throws IOException Failed to read authentication file.
	 */
	@NotNull
	private Optional<TwitchLogin> restoreAuthentication() throws IOException{
		if(!Files.exists(userAuthenticationFile)){
			return Optional.empty();
		}
		
		var twitchLogin = JacksonUtils.read(Files.newInputStream(userAuthenticationFile), new TypeReference<TwitchLogin>(){});
		
		var unirestConfig = Unirest.config();
		twitchLogin.getCookies().forEach(unirestConfig::addDefaultCookie);
		
		return Optional.of(twitchLogin);
	}
	
	/**
	 * Log in onto Twitch.
	 *
	 * @param loginRequest The login parameters to send
	 *
	 * @return Response received if it is a success.
	 *
	 * @throws LoginException Login failed.
	 */
	@NotNull
	private static HttpResponse<LoginResponse> login(@NotNull LoginRequest loginRequest) throws LoginException{
		log.debug("Sending passport login request");
		var response = Unirest.post(ENDPOINT + "/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", CLIENT_ID)
				.body(loginRequest)
				.asObject(LoginResponse.class);
		
		if(!response.isSuccess()){
			var statusCode = response.getStatus();
			
			var body = response.getBody();
			if(Objects.isNull(body)){
				throw new LoginException(statusCode, -1, "No body received");
			}
			
			var errorCode = body.getErrorCode();
			var errorDescription = body.getErrorDescription();
			if(Objects.isNull(errorCode)){
				throw new LoginException(statusCode, errorCode, errorDescription);
			}
			
			switch(errorCode){
				case 1000 -> throw new CaptchaSolveRequired(statusCode, errorCode, errorDescription);
				case 3001, 3003 -> throw new InvalidCredentials(statusCode, errorCode, errorDescription);
				case 3011, 3012 -> throw new MissingAuthy2FA(statusCode, errorCode, errorDescription);
				case 3022, 3023 -> throw new MissingTwitchGuard(statusCode, errorCode, errorDescription);
				default -> throw new LoginException(statusCode, errorCode, errorDescription);
			}
		}
		
		return response;
	}
}
