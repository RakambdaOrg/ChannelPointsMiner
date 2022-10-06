package fr.raksrinana.channelpointsminer.miner.browser;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.LoginException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Log4j2
public class BrowserController{
	@NotNull
	private final SelenideDriver driver;
	
	public void ensureLoggedIn(){
		openMainPage();
		
		if(!isLoggedIn()){
			throw new RuntimeException("Failed to restore cookies");
		}
	}
	
	private void openMainPage(){
		driver.open("https://www.twitch.tv/");
		
		var acceptCookiesButton = driver.$("button[data-a-target=consent-banner-accept]");
		if(acceptCookiesButton.is(Condition.visible)){
			acceptCookiesButton.click();
		}
	}
	
	public void login() throws LoginException{
		log.info("Logging in");
		openMainPage();
		
		if(isLoggedIn()){
			log.info("Already logged in");
			return;
		}
		
		askUserLogin();
		
		if(!isLoggedIn()){
			throw new LoginException("Not logged in");
		}
	}
	
	private void askUserLogin(){
		log.error("Not logged in, giving you 4 minutes to copy your cookies into the browser or log in, will resume after");
		try{
			Thread.sleep(4 * 60 * 1000);
		}
		catch(InterruptedException ignored){
		}
	}
	
	private boolean isLoggedIn(){
		var loginButton = getLoginButton();
		return !loginButton.is(Condition.visible);
	}
	
	@NotNull
	private SelenideElement getLoginButton(){
		return driver.$("button[data-a-target=login-button]");
	}
}
