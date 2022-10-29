package fr.rakambda.channelpointsminer.miner.api.gql.integrity.http;

import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityData;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityResponse;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.util.Objects;
import static kong.unirest.core.HeaderNames.AUTHORIZATION;

@RequiredArgsConstructor
@Log4j2
public class MobileIntegrityProvider implements IIntegrityProvider{
	private static final String ENDPOINT = "https://gql.twitch.tv/integrity";
	private static final String CLIENT_ID_HEADER = "Client-ID";
	private static final String CLIENT_SESSION_ID_HEADER = "Client-Session-ID";
	private static final String CLIENT_VERSION_HEADER = "Client-Version";
	private static final String X_DEVICE_ID_HEADER = "X-Device-ID";
	
	private static final String CLIENT_ID = "kd1unb4b3q4t58fwlpcbzcbnm76a8fp";
	private static final String CLIENT_VERSION = "32d439b2-bd5b-4e35-b82a-fae10b04da70";
	
	private final TwitchLogin twitchLogin;
	private final UnirestInstance unirest;
	
	private final String clientSessionId;
	private final String xDeviceId;
	
	private IntegrityData currentIntegrity;
	
	@Override
	@NotNull
	public IntegrityData getIntegrity() throws IntegrityException{
		synchronized(this){
			if(Objects.nonNull(currentIntegrity) && currentIntegrity.getExpiration().minus(Duration.ofMinutes(5)).isAfter(TimeFactory.now())){
				return currentIntegrity;
			}
			
			log.info("Querying new integrity token");
			var response = unirest.post(ENDPOINT)
					.header(AUTHORIZATION, "OAuth " + twitchLogin.getAccessToken())
					.header(CLIENT_ID_HEADER, CLIENT_ID)
					.header(CLIENT_SESSION_ID_HEADER, clientSessionId)
					.header(CLIENT_VERSION_HEADER, CLIENT_VERSION)
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
					.clientVersion(CLIENT_VERSION)
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
}
