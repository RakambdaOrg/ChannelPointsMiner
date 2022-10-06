package fr.raksrinana.channelpointsminer.miner.api.passport.browser;

import fr.raksrinana.channelpointsminer.miner.api.passport.IPassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.raksrinana.channelpointsminer.miner.config.BrowserConfiguration;
import fr.raksrinana.channelpointsminer.miner.factory.BrowserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import java.io.IOException;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public class BrowserPassportApi implements IPassportApi{
	private final BrowserConfiguration browserConfiguration;
	
	@Override
	@NotNull
	public TwitchLogin login() throws LoginException, IOException{
		log.info("Logging in");
		try(var browser = BrowserFactory.createBrowser(browserConfiguration)){
			var controller = browser.setup();
			controller.login();
			return extractPassportInfo(browser.getDriver().manage());
		}
	}
	
	@NotNull
	private TwitchLogin extractPassportInfo(WebDriver.Options manage) throws LoginException{
		var username = Optional.ofNullable(manage.getCookieNamed("login")).map(Cookie::getValue).orElseThrow(() -> new LoginException("Failed to get login info from browser, no username found"));
		var authToken = Optional.ofNullable(manage.getCookieNamed("auth-token")).map(Cookie::getValue).orElseThrow(() -> new LoginException("Failed to get login info from browser, no auth-token found"));
		
		return TwitchLogin.builder()
				.username(username)
				.accessToken(authToken)
				.cookies(manage.getCookies().stream()
						.map(c -> new kong.unirest.core.Cookie(c.toString()))
						.toList())
				.build();
	}
}
