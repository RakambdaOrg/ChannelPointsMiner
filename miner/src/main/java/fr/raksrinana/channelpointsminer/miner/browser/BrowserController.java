package fr.raksrinana.channelpointsminer.miner.browser;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.raksrinana.channelpointsminer.miner.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import static org.openqa.selenium.By.id;

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
	
	public void login(@NotNull String username, @NotNull String password) throws LoginException{
		log.info("Logging in");
		openMainPage();
		
		if(isLoggedIn()){
			log.info("Already logged in");
			return;
		}
		
		getLoginButton().click();
		driver.$(id("login-username")).scrollTo().setValue(username);
		driver.$(id("password-input")).scrollTo().setValue(password);
		driver.$("button[data-a-target=passport-login-button]").scrollTo().click();
		
		var serverErrorAlert = driver.$(".server-message-alert");
		if(serverErrorAlert.is(Condition.visible)){
			throw new LoginException("Failed to login: " + serverErrorAlert.text());
		}
		
		var twoFactorInput = driver.$("input[data-a-target=tw-input]");
		if(twoFactorInput.is(Condition.visible)){
			twoFactorInput.scrollTo().setValue(CommonUtils.getUserInput("Enter 2FA token for user " + username + ":"));
			
			driver.$("input[data-a-target=tw-checkbox]").scrollTo().click();
			driver.$("button[screen=two_factor,target=submit_button]").scrollTo().click();
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
