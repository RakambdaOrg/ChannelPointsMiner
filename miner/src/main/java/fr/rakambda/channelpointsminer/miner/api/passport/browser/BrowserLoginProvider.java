package fr.rakambda.channelpointsminer.miner.api.passport.browser;

import fr.rakambda.channelpointsminer.miner.api.passport.ILoginProvider;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.rakambda.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.rakambda.channelpointsminer.miner.factory.BrowserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public class BrowserLoginProvider implements ILoginProvider{
	private final BrowserConfiguration browserConfiguration;
	
	@Override
	@NotNull
	public TwitchLogin login() throws LoginException{
		log.info("Logging in");
		try(var browser = BrowserFactory.createBrowser(browserConfiguration)){
			var controller = browser.setup();
			var cookiesPath = Optional.ofNullable(browserConfiguration.getCookiesPath()).map(Paths::get).orElse(null);
			
			controller.login(cookiesPath);
			return extractPassportInfo(browser.getDriver().manage());
		}
		catch(IOException e){
			throw new LoginException("Failed to login", e);
		}
	}
	
	@NotNull
	private TwitchLogin extractPassportInfo(WebDriver.Options manage) throws LoginException{
		var username = Optional.ofNullable(manage.getCookieNamed("login")).map(Cookie::getValue).orElseThrow(() -> new LoginException("Failed to get login info from browser, no username found"));
		var authToken = Optional.ofNullable(manage.getCookieNamed("auth-token")).map(Cookie::getValue).orElseThrow(() -> new LoginException("Failed to get login info from browser, no auth-token found"));
		
		var cookies = manage.getCookies().stream()
				.map(c -> new kong.unirest.core.Cookie(c.toString()))
				.toList();
		
		return TwitchLogin.builder()
				.twitchClient(TwitchClient.WEB)
				.username(username)
				.accessToken(authToken)
				.cookies(cookies)
				.build();
	}
}
