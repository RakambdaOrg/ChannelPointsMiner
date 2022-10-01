package fr.raksrinana.channelpointsminer.miner.api.passport.browser;

import fr.raksrinana.channelpointsminer.miner.api.passport.IPassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.raksrinana.channelpointsminer.miner.config.BrowserConfiguration;
import fr.raksrinana.channelpointsminer.miner.factory.BrowserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class BrowserPassportApi implements IPassportApi{
	private final BrowserConfiguration browserConfiguration;
	private final String username;
	private final String password;
	
	@Override
	@NotNull
	public TwitchLogin login() throws LoginException, IOException{
		log.info("Logging in");
		try(var browser = BrowserFactory.createBrowser(browserConfiguration)){
			var controller = browser.setup();
			controller.login(username, password);
			return controller.extractPassportInfo().orElseThrow(() -> new LoginException("Failed to get login info from browser"));
		}
	}
}
