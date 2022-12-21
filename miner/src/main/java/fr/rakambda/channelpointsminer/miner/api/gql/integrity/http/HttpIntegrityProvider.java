package fr.rakambda.channelpointsminer.miner.api.gql.integrity.http;

import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityData;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.version.IVersionProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.version.VersionException;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import static kong.unirest.core.HeaderNames.AUTHORIZATION;

@RequiredArgsConstructor
@Log4j2
public class HttpIntegrityProvider implements IIntegrityProvider{
	private static final String ENDPOINT = "https://gql.twitch.tv/integrity";
	private static final String CLIENT_ID_HEADER = "Client-ID";
	private static final String CLIENT_SESSION_ID_HEADER = "Client-Session-ID";
	private static final String CLIENT_VERSION_HEADER = "Client-Version";
	private static final String X_DEVICE_ID_HEADER = "X-Device-ID";
	
	private static final String CLIENT_ID = TwitchClient.WEB.getClientId();
	private static final String DEFAULT_CLIENT_VERSION = "ef928475-9403-42f2-8a34-55784bd08e16";
	
	private final TwitchLogin twitchLogin;
	private final UnirestInstance unirest;
	private final IVersionProvider versionProvider;
	
	private final String clientSessionId;
	private final String xDeviceId;
	
	private IntegrityData currentIntegrity;
	
	@Override
	@NotNull
	public Optional<IntegrityData> getIntegrity() throws IntegrityException{
		synchronized(this){
			if(Objects.nonNull(currentIntegrity) && currentIntegrity.getExpiration().minus(Duration.ofMinutes(5)).isAfter(TimeFactory.now())){
				return Optional.of(currentIntegrity);
			}
			
			var clientVersion = getClientVersion();
			
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
			return Optional.of(currentIntegrity);
		}
	}
	
	@Override
	public void invalidate(){
		log.info("Invalidating integrity");
		currentIntegrity = null;
	}
	
	@NotNull
	private String getClientVersion(){
		try{
			return versionProvider.getVersion();
		}
		catch(VersionException e){
			log.error("Failed to get twitch version", e);
			return DEFAULT_CLIENT_VERSION;
		}
	}
}
