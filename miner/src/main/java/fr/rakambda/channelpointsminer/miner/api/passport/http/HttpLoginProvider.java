package fr.rakambda.channelpointsminer.miner.api.passport.http;

import fr.rakambda.channelpointsminer.miner.api.passport.ILoginProvider;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLoginCacher;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.CaptchaSolveRequired;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.InvalidCredentials;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.MissingAuthy2FA;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.MissingTwitchGuard;
import fr.rakambda.channelpointsminer.miner.api.passport.http.data.LoginRequest;
import fr.rakambda.channelpointsminer.miner.api.passport.http.data.LoginResponse;
import fr.rakambda.channelpointsminer.miner.config.login.IPassportApiLoginProvider;
import fr.rakambda.channelpointsminer.miner.event.impl.LoginRequiredEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.UnirestInstance;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Objects;
import static fr.rakambda.channelpointsminer.miner.util.CommonUtils.getUserInput;
import static kong.unirest.core.ContentType.APPLICATION_JSON;
import static kong.unirest.core.HeaderNames.CONTENT_TYPE;

@Log4j2
public class HttpLoginProvider implements ILoginProvider{
	private static final String ENDPOINT = "https://passport.twitch.tv";
	private final UnirestInstance unirest;
	private final TwitchClient twitchClient;
	private final String username;
	private final TwitchLoginCacher twitchLoginCacher;
	private final String password;
	private final boolean ask2FA;
	private final IEventManager eventManager;
	
	public HttpLoginProvider(@NotNull TwitchClient twitchClient, @NotNull UnirestInstance unirest, @NotNull String username, @NotNull IPassportApiLoginProvider passportApiLoginProvider, @NotNull TwitchLoginCacher twitchLoginCacher, @NotNull IEventManager eventManager){
		this.twitchClient = twitchClient;
		this.unirest = unirest;
		this.username = username;
		this.twitchLoginCacher = twitchLoginCacher;
		this.eventManager = eventManager;
		
		password = passportApiLoginProvider.getPassword();
		ask2FA = passportApiLoginProvider.isUse2Fa();
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
		var restoredAuthOptional = twitchLoginCacher.restoreAuthentication();
		if(restoredAuthOptional.isPresent()){
			log.info("Logged back in from authentication file");
			var restoredAuth = restoredAuthOptional.get();
			
			if(restoredAuth.getTwitchClient() != twitchClient){
				throw new LoginException("Restored authentication is for another twitch client, use another auth folder");
			}
			
			return restoredAuth;
		}
		
		HttpResponse<LoginResponse> response;
		try{
			if(ask2FA){
				response = twoFactorLogin();
			}
			else{
				response = login(LoginRequest.builder()
						.clientId(twitchClient.getClientId())
						.username(username)
						.password(password)
						.build());
			}
		}
		catch(MissingAuthy2FA e){
			response = twoFactorLogin();
		}
		catch(MissingTwitchGuard e){
			eventManager.onEvent(new LoginRequiredEvent(TimeFactory.now(), "Twitch guard code required"));
			response = login(LoginRequest.builder()
					.clientId(twitchClient.getClientId())
					.username(username)
					.password(password)
					.twitchGuardCode(getUserInput("Enter TwitchGuard code:"))
					.build());
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
		eventManager.onEvent(new LoginRequiredEvent(TimeFactory.now(), "2FA required"));
		var authToken = getUserInput("Enter 2FA token for user " + username + ":");
		return login(LoginRequest.builder()
				.clientId(twitchClient.getClientId())
				.username(username)
				.password(password)
				.authyToken(authToken)
				.build());
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
	private HttpResponse<LoginResponse> login(@NotNull LoginRequest loginRequest) throws LoginException{
		log.debug("Sending passport login request");
		var response = unirest.post(ENDPOINT + "/login")
				.header(CONTENT_TYPE, APPLICATION_JSON.toString())
				.header("Client-Id", twitchClient.getClientId())
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
				throw new LoginException(statusCode, null, errorDescription);
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
				.twitchClient(twitchClient)
				.username(username)
				.accessToken(response.getBody().getAccessToken())
				.cookies(response.getCookies())
				.build();
		twitchLoginCacher.saveAuthentication(twitchLogin);
		return twitchLogin;
	}
}
