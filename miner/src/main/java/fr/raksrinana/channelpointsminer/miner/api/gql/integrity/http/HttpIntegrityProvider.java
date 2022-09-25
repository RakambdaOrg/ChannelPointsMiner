package fr.raksrinana.channelpointsminer.miner.api.gql.integrity.http;

import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IntegrityData;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Pattern;
import static kong.unirest.core.HeaderNames.AUTHORIZATION;

@RequiredArgsConstructor
@Log4j2
public class HttpIntegrityProvider implements IIntegrityProvider{
	private static final String ENDPOINT = "https://gql.twitch.tv/integrity";
	private static final String CLIENT_ID_HEADER = "Client-ID";
	private static final String CLIENT_SESSION_ID_HEADER = "Client-Session-ID";
	private static final String CLIENT_VERSION_HEADER = "Client-Version";
	private static final String X_DEVICE_ID_HEADER = "X-Device-ID";
	
	private static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
	
	private static final Pattern TWILIGHT_BUILD_ID_PATTERN = Pattern.compile("window\\.__twilightBuildID=\"([0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-4[0-9A-Fa-f]{3}-[89ABab][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12})\";");
	
	private final TwitchLogin twitchLogin;
	private final UnirestInstance unirest;
	
	private final String clientSessionId;
	private final String xDeviceId;
	
	private String clientVersion = "ef928475-9403-42f2-8a34-55784bd08e16";
	private IntegrityData currentIntegrity;
	
	@Override
	@NotNull
	public IntegrityData getIntegrity() throws IntegrityException{
		synchronized(this){
			if(Objects.nonNull(currentIntegrity) && currentIntegrity.getExpiration().minus(Duration.ofMinutes(5)).isAfter(TimeFactory.now())){
				return currentIntegrity;
			}
			
			updateClientVersion();
			
			log.info("Querying new integrity token");
			var response = unirest.post(ENDPOINT)
					.header(AUTHORIZATION, "OAuth " + twitchLogin.getAccessToken())
					.header(CLIENT_ID_HEADER, CLIENT_ID)
					.header(CLIENT_SESSION_ID_HEADER, clientSessionId)
					.header(CLIENT_VERSION_HEADER, clientVersion)
					.header(X_DEVICE_ID_HEADER, xDeviceId)
					.asObject(IntegrityResponse.class);
			
			if(!response.isSuccess()){
				throw new IntegrityException(response.getStatus(), "Http code is not a success");
			}
			
			var body = response.getBody();
			if(Objects.isNull(body.getToken())){
				throw new IntegrityException(response.getStatus(), body.getMessage());
			}
			
			log.info("New integrity token will expire at {}", body.getExpiration());
			currentIntegrity = IntegrityData.builder()
					.token(body.getToken())
					.expiration(body.getExpiration())
					.clientSessionId(clientSessionId)
					.clientVersion(clientVersion)
					.xDeviceId(xDeviceId)
					.build();
			return currentIntegrity;
		}
	}
	
	@Override
	public void invalidate(){
		log.info("Invalidating integrity");
		currentIntegrity = null;
	}
	
	private void updateClientVersion(){
		log.info("Querying new client version");
		var response = unirest.get("https://www.twitch.tv").asString();
		if(!response.isSuccess()){
			log.warn("Failed to update client version, status is : " + response.getStatus());
			return;
		}
		
		var page = response.getBody();
		if(Objects.isNull(page)){
			log.warn("Failed to update client version, null page");
			return;
		}
		
		var matcher = TWILIGHT_BUILD_ID_PATTERN.matcher(page);
		if(!matcher.find()){
			log.warn("Failed to update client version, didn't find version in page");
			return;
		}
		
		clientVersion = matcher.group(1);
		log.info("Current client version is {}", clientVersion);
	}
}
