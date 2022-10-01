package fr.raksrinana.channelpointsminer.miner.api.gql.integrity.browser;

import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IntegrityData;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.raksrinana.channelpointsminer.miner.config.BrowserConfiguration;
import fr.raksrinana.channelpointsminer.miner.factory.BrowserFactory;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.util.Objects;

@RequiredArgsConstructor
@Log4j2
public class BrowserIntegrityProvider implements IIntegrityProvider{
	private final BrowserConfiguration browserConfiguration;
	
	private IntegrityData currentIntegrity;
	
	@Override
	public void invalidate(){
		log.info("Invalidating integrity");
		currentIntegrity = null;
	}
	
	@Override
	@NotNull
	public IntegrityData getIntegrity() throws IntegrityException{
		synchronized(this){
			if(Objects.nonNull(currentIntegrity) && currentIntegrity.getExpiration().minus(Duration.ofMinutes(5)).isAfter(TimeFactory.now())){
				return currentIntegrity;
			}
			
			log.info("Querying new integrity token");
			try(var browser = BrowserFactory.createBrowser(browserConfiguration)){
				var controller = browser.setup();
				controller.ensureLoggedIn();
				currentIntegrity = controller.extractGQLIntegrity().orElseThrow(() -> new IntegrityException("Failed to get integrity token from browser"));
				return currentIntegrity;
			}
		}
	}
}
