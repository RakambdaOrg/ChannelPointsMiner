package fr.rakambda.channelpointsminer.miner.browser;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import fr.rakambda.channelpointsminer.miner.api.passport.exceptions.LoginException;
import fr.rakambda.channelpointsminer.miner.event.impl.LoginRequiredEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Cookie;
import tools.jackson.core.type.TypeReference;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
@Log4j2
public class BrowserController{
	@NonNull
	private final SelenideDriver driver;
	@NonNull
	private final IEventManager eventManager;
	
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
		login(null);
	}
	
	public void login(@Nullable Path cookiesPath) throws LoginException, IOException{
		log.info("Logging in");
		openTurboPage();
		
		if(isLoggedIn()){
			log.info("Already logged in");
			return;
		}
		
		var manager = driver.getWebDriver().manage();
		var userInput = askUserLogin(cookiesPath);
		if(Objects.nonNull(userInput)){
			JacksonUtils.read(userInput, new TypeReference<List<CookieData>>(){})
					.stream()
					.map(c -> new Cookie(c.getName(), c.getValue(), c.getDomain(), c.getPath(), c.getExpiry(), c.isSecure(), c.isHttpOnly(), c.getSameSite()))
					.forEach(manager::addCookie);
		}
		
		driver.refresh();
		CommonUtils.randomSleep(5000, 1);
		if(!isLoggedIn()){
			throw new LoginException("Not logged in");
		}
	}
	
	@SneakyThrows
	@Nullable
	private String askUserLogin(@Nullable Path cookiesFile){
		log.error("Not logged in, please input cookies");
		
		if(Objects.nonNull(cookiesFile)){
			log.info("User defined cookies file, using it");
			if(!Files.exists(cookiesFile)){
				throw new FileNotFoundException("File does not exist: " + cookiesFile.toAbsolutePath());
			}
			return Files.readString(cookiesFile);
		}
		
		try{
			eventManager.onEvent(new LoginRequiredEvent(TimeFactory.now(), "Cookies input required"));
			return CommonUtils.getUserInput("Provide your session cookies under JSON format (1 line only) (you can use an extension like Cookie-Editor): ");
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
	
	@NonNull
	private SelenideElement getLoginButton(){
		return driver.$("button[data-a-target=login-button]");
	}
}
