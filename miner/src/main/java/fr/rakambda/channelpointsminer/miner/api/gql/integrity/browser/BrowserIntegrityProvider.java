package fr.rakambda.channelpointsminer.miner.api.gql.integrity.browser;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityData;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IntegrityResponse;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.rakambda.channelpointsminer.miner.browser.Browser;
import fr.rakambda.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.rakambda.channelpointsminer.miner.factory.BrowserFactory;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.devtools.v107.page.model.FrameId;
import java.io.IOException;
import java.time.Duration;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
public class BrowserIntegrityProvider implements IIntegrityProvider{
	public static final String INTEGRITY_URL = "https://gql.twitch.tv/integrity";
	private final BrowserConfiguration browserConfiguration;
	
	private IntegrityData currentIntegrity;
	
	@Override
	public void invalidate(){
		log.info("Invalidating integrity");
		currentIntegrity = null;
	}
	
	@Override
	@NotNull
	public Optional<IntegrityData> getIntegrity() throws IntegrityException{
		synchronized(this){
			if(Objects.nonNull(currentIntegrity) && currentIntegrity.getExpiration().minus(Duration.ofMinutes(5)).isAfter(TimeFactory.now())){
				return Optional.of(currentIntegrity);
			}
			
			log.info("Querying new integrity token");
			try(var browser = BrowserFactory.createBrowser(browserConfiguration)){
				var controller = browser.setup();
				controller.ensureLoggedIn();
				CommonUtils.randomSleep(10000, 1);
				currentIntegrity = extractGQLIntegrity(browser);
				log.debug("Got new integrity token {}", currentIntegrity);
				return Optional.of(currentIntegrity);
			}
			catch(LoginException e){
				throw new IntegrityException("Failed to get integrity", e);
			}
		}
	}
	
	@NotNull
	private IntegrityData extractGQLIntegrity(@NotNull Browser browser) throws IntegrityException{
		var integrityResponse = browser.getReceivedResponses().stream()
				.filter(r -> Objects.equals(r.getResponse().getUrl(), INTEGRITY_URL))
				.max(Comparator.comparing(r -> r.getTimestamp().toString()))
				.orElseThrow(() -> new IntegrityException("Failed to get integrity token from browser, no response found"));
		var frameId = integrityResponse.getFrameId().orElseThrow(() -> new IntegrityException("Failed to get integrity token from browser, no frame id found")).toString();
		var integrityRequest = browser.getSentRequests().stream()
				.filter(r -> Objects.equals(r.getRequest().getUrl(), INTEGRITY_URL))
				.filter(r -> Objects.equals(r.getFrameId().map(FrameId::toString).orElse(null), frameId))
				.findAny()
				.orElseThrow(() -> new IntegrityException("Failed to get integrity token from browser, no request found"));
		var responseBody = browser.getRequestBody(integrityResponse.getRequestId());
		try{
			var responseData = JacksonUtils.read(responseBody, new TypeReference<IntegrityResponse>(){});
			var clientSessionId = Optional.ofNullable(integrityRequest.getRequest().getHeaders().get("Client-Session-Id"))
					.map(String.class::cast)
					.orElseThrow(() -> new IntegrityException("No client session id found"));
			var clientVersion = Optional.ofNullable(integrityRequest.getRequest().getHeaders().get("Client-Version"))
					.map(String.class::cast)
					.orElseThrow(() -> new IntegrityException("No client version found"));
			var xDeviceId = Optional.ofNullable(integrityRequest.getRequest().getHeaders().get("X-Device-Id"))
					.map(String.class::cast)
					.orElseThrow(() -> new IntegrityException("No x device id found"));
			
			return IntegrityData.builder()
					.clientSessionId(clientSessionId)
					.clientVersion(clientVersion)
					.token(responseData.getToken())
					.expiration(responseData.getExpiration())
					.xDeviceId(xDeviceId)
					.build();
		}
		catch(IOException e){
			throw new IntegrityException("Failed to get integrity token from browser, failed to parse data", e);
		}
	}
}
