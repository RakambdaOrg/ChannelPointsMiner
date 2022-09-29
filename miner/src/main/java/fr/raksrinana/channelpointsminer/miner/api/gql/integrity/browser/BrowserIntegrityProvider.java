package fr.raksrinana.channelpointsminer.miner.api.gql.integrity.browser;

import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IntegrityData;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IntegrityException;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.config.BrowserConfiguration;
import fr.raksrinana.channelpointsminer.miner.factory.BrowserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Cookie;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
public class BrowserIntegrityProvider implements IIntegrityProvider{
	private final TwitchLogin twitchLogin;
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
		try(var browser = BrowserFactory.createBrowser(browserConfiguration)){
			var cookies = twitchLogin.getCookies().stream()
					.map(c -> new Cookie(c.getName(),
							c.getValue(),
							c.getDomain(),
							c.getPath(),
							Optional.ofNullable(c.getExpiration()).map(z -> new Date(z.toEpochSecond())).orElse(null),
							c.isSecure()))
					.toList();
			
			var controller = browser.setup(cookies);
			controller.ensureLoggedIn();
			
			throw new IntegrityException("To do");
		}
	}
}
