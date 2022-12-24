package fr.rakambda.channelpointsminer.miner.api.passport.browser;

import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.rakambda.channelpointsminer.miner.browser.Browser;
import fr.rakambda.channelpointsminer.miner.browser.BrowserController;
import fr.rakambda.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.rakambda.channelpointsminer.miner.factory.BrowserFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrowserPassportApiTest{
	private static final String USERNAME = "username";
	private static final String ACCESS_TOKEN = "access-token";
	private static final List<kong.unirest.core.Cookie> COOKIES = List.of(
			new kong.unirest.core.Cookie("cookie1", "value1"),
			new kong.unirest.core.Cookie("cookie2", "value2")
	);
	
	@InjectMocks
	private BrowserLoginProvider tested;
	
	@Mock
	private BrowserConfiguration browserConfiguration;
	@Mock
	private Browser browser;
	@Mock
	private BrowserController browserController;
	@Mock
	private WebDriver webDriver;
	@Mock
	private WebDriver.Options manager;
	
	@BeforeEach
	void setUp(){
		lenient().when(browser.setup()).thenReturn(browserController);
		lenient().when(browser.getDriver()).thenReturn(webDriver);
		lenient().when(webDriver.manage()).thenReturn(manager);
		
		lenient().when(manager.getCookieNamed("login")).thenReturn(new Cookie("login", USERNAME));
		lenient().when(manager.getCookieNamed("auth-token")).thenReturn(new Cookie("auth-token", ACCESS_TOKEN));
		lenient().when(manager.getCookies()).thenReturn(Set.of(
				new Cookie("cookie1", "value1"),
				new Cookie("cookie2", "value2")
		));
	}
	
	@Test
	void loginIsExtracted() throws LoginException{
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			assertThat(tested.login()).isEqualTo(TwitchLogin.builder()
					.twitchClient(TwitchClient.WEB)
					.username(USERNAME)
					.accessToken(ACCESS_TOKEN)
					.cookies(COOKIES)
					.build());
		}
	}
	
	@Test
	void loginIsExtractedWithCookies() throws LoginException, IOException{
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			var pathStr = "/path/to/cookies.json";
			when(browserConfiguration.getCookiesPath()).thenReturn(pathStr);
			
			assertThat(tested.login()).isEqualTo(TwitchLogin.builder()
					.twitchClient(TwitchClient.WEB)
					.username(USERNAME)
					.accessToken(ACCESS_TOKEN)
					.cookies(COOKIES)
					.build());
			
			verify(browserController).login(Paths.get(pathStr));
		}
	}
	
	@Test
	void exceptionBecauseNoLogin(){
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			when(manager.getCookieNamed("login")).thenReturn(null);
			
			assertThrows(LoginException.class, tested::login);
		}
	}
	
	@Test
	void exceptionBecauseNoAuth(){
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			when(manager.getCookieNamed("auth-token")).thenReturn(null);
			
			assertThrows(LoginException.class, tested::login);
		}
	}
	
	@Test
	void onExceptionLoggingIn() throws LoginException, IOException{
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			doThrow(new LoginException("For tests")).when(browserController).login(null);
			
			assertThrows(LoginException.class, tested::login);
		}
	}
	
	@Test
	void onExceptionCreatingBrowser(){
		try(var browserFactory = mockStatic(BrowserFactory.class)){
			browserFactory.when(() -> BrowserFactory.createBrowser(browserConfiguration)).thenReturn(browser);
			
			when(browser.setup()).thenThrow(new RuntimeException("For tests"));
			
			assertThrows(RuntimeException.class, tested::login);
		}
	}
}