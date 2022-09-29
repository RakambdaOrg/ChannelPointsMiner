package fr.raksrinana.channelpointsminer.miner.api.gql.integrity.browser;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Cookie;
import java.util.Collection;

@RequiredArgsConstructor
@Log4j2
public class BrowserController{
	@NotNull
	private final SelenideDriver driver;
	@NotNull
	private final Collection<Cookie> cookies;
	
	public void ensureLoggedIn(){
		openMainPage();
		
		var acceptCookiesButton = driver.$("button[data-a-target=consent-banner-accept]");
		if(acceptCookiesButton.is(Condition.visible)){
			acceptCookiesButton.click();
		}
		
		if(!isLoggedIn()){
			restoreCookies();
			openMainPage();
			
			if(!isLoggedIn()){
				throw new RuntimeException("Failed to restore cookies");
			}
		}
	}
	
	private void openMainPage(){
		driver.open("https://www.twitch.tv/");
	}
	
	private boolean isLoggedIn(){
		var loginButton = driver.$("button[data-a-target=login-button]");
		return !loginButton.is(Condition.visible);
	}
	
	private void restoreCookies(){
		log.info("Restoring cookies");
		
		var cookieManager = driver.getWebDriver().manage();
		cookies.forEach(cookieManager::addCookie);
	}
}
