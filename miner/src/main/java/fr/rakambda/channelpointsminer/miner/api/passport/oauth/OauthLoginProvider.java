package fr.rakambda.channelpointsminer.miner.api.passport.oauth;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.rakambda.channelpointsminer.miner.api.passport.ILoginProvider;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLoginCacher;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.rakambda.channelpointsminer.miner.api.passport.oauth.data.DeviceResponse;
import fr.rakambda.channelpointsminer.miner.api.passport.oauth.data.TokenResponse;
import fr.rakambda.channelpointsminer.miner.config.login.IOauthApiLoginProvider;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.event.impl.LoginRequiredEvent;
import kong.unirest.core.UnirestInstance;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Log4j2
public class OauthLoginProvider implements ILoginProvider{
	private static final String ENDPOINT = "https://id.twitch.tv/oauth2/";
	private static final String SCOPES = "channel_read chat:read user_blocks_edit user_blocks_read user_follows_edit user_read";
	private static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:device_code";
	
	private final TwitchClient twitchClient;
	private final UnirestInstance unirest;
	private final String username;
	private final TwitchLoginCacher twitchLoginCacher;
	private final IEventManager eventManager;
	
	public OauthLoginProvider(@NotNull TwitchClient twitchClient, @NotNull UnirestInstance unirest, @NotNull String username, @NotNull TwitchLoginCacher twitchLoginCacher, @NotNull IEventManager eventManager){
		this.twitchClient = twitchClient;
		this.unirest = unirest;
		this.username = username;
		this.twitchLoginCacher = twitchLoginCacher;
		this.eventManager = eventManager;
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
		
		DeviceResponse deviceToken = generateDeviceToken();
		eventManager.onEvent(new LoginRequiredEvent(TimeFactory.now()));
		log.info("Please open page {} and provide the following token within {}: {}",
				deviceToken.getVerificationUri(),
				Duration.ofSeconds(deviceToken.getExpiresIn()),
				deviceToken.getUserCode());
		
		TokenResponse tokenResponse = fetchToken(deviceToken.getDeviceCode(), Duration.ofSeconds(deviceToken.getInterval()), TimeFactory.now().plus(deviceToken.getExpiresIn(), ChronoUnit.SECONDS));
		
		log.info("Logged in");
		return handleResponse(tokenResponse);
	}
	
	@NotNull
	private DeviceResponse generateDeviceToken() throws LoginException{
		var response = unirest.post(ENDPOINT + "device")
				.field("client_id", twitchClient.getClientId())
				.field("scopes", SCOPES)
				.asObject(DeviceResponse.class);
		
		if(!response.isSuccess()){
			var statusCode = response.getStatus();
			
			var body = response.getBody();
			if(Objects.isNull(body)){
				throw new LoginException(statusCode, -1, "No body received when generating device token");
			}
			
			var errorDescription = body.getMessage();
			throw new LoginException(statusCode, -1, "Failed to generate device token: " + errorDescription);
		}
		
		return response.getBody();
	}
	
	@NotNull
	@SneakyThrows(InterruptedException.class)
	private TokenResponse fetchToken(@NotNull String deviceCode, @NotNull Duration interval, @NotNull Instant limit) throws LoginException{
		while(TimeFactory.now().isBefore(limit)){
			var response = unirest.post(ENDPOINT + "token")
					.field("client_id", twitchClient.getClientId())
					.field("grant_type", GRANT_TYPE)
					.field("device_code", deviceCode)
					.asObject(TokenResponse.class);
			
			if(response.isSuccess()){
				return response.getBody();
			}
			
			var statusCode = response.getStatus();
			
			var body = response.getBody();
			if(Objects.isNull(body)){
				throw new LoginException(statusCode, -1, "No body received when getting auth token");
			}
			
			if(statusCode == 400 && body.isAuthorizationPending()){
				Thread.sleep(interval.toMillis());
			}
			else{
				var errorDescription = body.getMessage();
				throw new LoginException(statusCode, -1, "Failed to get auth token: " + errorDescription);
			}
		}
		
		throw new LoginException(400, -1, "Couldn't get auth token in time");
	}
	
	/**
	 * @param response Response.
	 *
	 * @return {@link TwitchLogin}.
	 *
	 * @throws IOException File failed to write.
	 */
	@NotNull
	private TwitchLogin handleResponse(@NotNull TokenResponse response) throws IOException{
		var twitchLogin = TwitchLogin.builder()
				.twitchClient(twitchClient)
				.username(username)
				.accessToken(response.getAccessToken())
				.cookies(List.of())
				.build();
		twitchLoginCacher.saveAuthentication(twitchLogin);
		return twitchLogin;
	}
}
