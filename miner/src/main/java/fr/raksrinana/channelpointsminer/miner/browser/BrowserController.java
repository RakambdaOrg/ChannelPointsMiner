package fr.raksrinana.channelpointsminer.miner.browser;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.raksrinana.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.raksrinana.channelpointsminer.miner.util.CommonUtils;
import fr.raksrinana.channelpointsminer.miner.util.json.JacksonUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.Cookie;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
@Log4j2
public class BrowserController{
	@NotNull
	private final SelenideDriver driver;
	
	public void ensureLoggedIn() throws LoginException{
		openTurboPage();
		
		if(!isLoggedIn()){
			throw new LoginException("Not logged in");
		}
	}
	
	private void openTurboPage(){
		driver.open("https://www.twitch.tv/turbo");
		
		var acceptCookiesButton = driver.$("button[data-a-target=consent-banner-accept]");
		if(acceptCookiesButton.is(Condition.visible)){
			acceptCookiesButton.click();
		}
	}
	
	public void login() throws LoginException, IOException{
		log.info("Logging in");
		openTurboPage();
		
		if(isLoggedIn()){
			log.info("Already logged in");
			return;
		}
		
		var manager = driver.getWebDriver().manage();
		var userInput = askUserLogin();
		if(Objects.nonNull(userInput)){
			JacksonUtils.read(userInput, new TypeReference<List<CookieData>>(){})
					.stream()
					.map(c -> new Cookie(c.getName(), c.getValue(), c.getDomain(), c.getPath(), c.getExpiry(), c.isSecure(), c.isHttpOnly(), c.getSameSite()))
					.forEach(manager::addCookie);
		}
		
		driver.refresh();
		if(!isLoggedIn()){
			throw new LoginException("Not logged in");
		}
	}
	
	@SneakyThrows
	@Nullable
	private String askUserLogin(){
		log.error("Not logged in, please input cookies");
		try{
			return CommonUtils.getUserInput("Provide your session cookies under JSON format (you can use an extension like Cookie-Editor): ");
		}
		catch(NoSuchElementException e){
			log.warn("Couldn't get user input, seems like you're in a containerized environment. Giving you 4 minutes to manually log in into the browser manually.");
			Thread.sleep(4 * 60 * 1000);
			return null;
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
