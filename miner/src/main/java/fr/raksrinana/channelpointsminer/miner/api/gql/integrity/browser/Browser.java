package fr.raksrinana.channelpointsminer.miner.api.gql.integrity.browser;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import fr.raksrinana.channelpointsminer.miner.config.BrowserConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Log4j2
@RequiredArgsConstructor
public class Browser implements AutoCloseable{
	private final BrowserConfiguration browserConfiguration;
	
	private WebDriver driver;
	private SelenideDriver selenideDriver;
	
	@NotNull
	public BrowserController setup(Collection<Cookie> cookies){
		log.info("Starting browser...");
		
		var config = setupSelenideConfig(new SelenideConfig());
		driver = getDriver(browserConfiguration);
		driver = new Augmenter().augment(driver);
		
		selenideDriver = new SelenideDriver(config, driver, null);
		
		driver.manage().window().maximize();
		
		//Remove navigator.webdriver Flag using JavaScript
		selenideDriver.executeJavaScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
		
		if(!(driver instanceof HasDevTools devToolsDriver)){
			throw new IllegalStateException("Browser must have dev tools support");
		}
		var devTools = devToolsDriver.getDevTools();
		devTools.createSession();
		devTools.send(new Command<>("Page.addScriptToEvaluateOnNewDocument", Map.of("source", """
				Object.defineProperty(navigator, 'webdriver', {
					get: () => undefined
				})""")));
		
		return new BrowserController(selenideDriver, cookies);
	}
	
	@NotNull
	private SelenideConfig setupSelenideConfig(@NotNull SelenideConfig config){
		config.savePageSource(false);
		config.screenshots(browserConfiguration.isScreenshots());
		config.headless(browserConfiguration.isHeadless());
		config.holdBrowserOpen(false);
		return config;
	}
	
	private WebDriver getDriver(@NotNull BrowserConfiguration config){
		return switch(config.getDriver()){
			case CHROME -> getChromeDriver(config);
			case FIREFOX -> getFirefoxDriver(config);
			case REMOTE_CHROME -> getRemoteDriverChrome(config);
			case REMOTE_FIREFOX -> getRemoteDriverFirefox(config);
		};
	}
	
	@NotNull
	private ChromeDriver getChromeDriver(@NotNull BrowserConfiguration configuration){
		return new ChromeDriver(getDefaultChromeOptions(configuration));
	}
	
	@NotNull
	private FirefoxDriver getFirefoxDriver(@NotNull BrowserConfiguration configuration){
		return new FirefoxDriver(getDefaultFirefoxOptions(configuration));
	}
	
	@SneakyThrows
	@NotNull
	private RemoteWebDriver getRemoteDriverChrome(@NotNull BrowserConfiguration configuration){
		return new RemoteWebDriver(new URL(configuration.getRemoteHost()), getDefaultChromeOptions(configuration));
	}
	
	@SneakyThrows
	@NotNull
	private RemoteWebDriver getRemoteDriverFirefox(@NotNull BrowserConfiguration configuration){
		return new RemoteWebDriver(new URL(configuration.getRemoteHost()), getDefaultFirefoxOptions(configuration));
	}
	
	@NotNull
	private ChromeOptions getDefaultChromeOptions(@NotNull BrowserConfiguration configuration){
		var options = new ChromeOptions();
		Optional.ofNullable(configuration.getBinary()).ifPresent(binary -> options.setBinary(Paths.get(binary).toFile()));
		options.setHeadless(configuration.isHeadless());
		Optional.ofNullable(configuration.getUserAgent()).map("user-agent=\"%s\""::formatted).ifPresent(options::addArguments);
		Optional.ofNullable(configuration.getUserDir()).map(ud -> ud.replace(" ", "\\ ")).map("user-data-dir=%s"::formatted).ifPresent(options::addArguments);
		options.addArguments("--disable-blink-features=AutomationControlled");
		options.addArguments("disable-infobars");
		options.addArguments("disable-popup-blocking");
		options.setExperimentalOption("excludeSwitches", Set.of("enable-automation"));
		options.setExperimentalOption("useAutomationExtension", false);
		return options;
	}
	
	@NotNull
	private FirefoxOptions getDefaultFirefoxOptions(@NotNull BrowserConfiguration configuration){
		var options = new FirefoxOptions();
		Optional.ofNullable(configuration.getBinary()).ifPresent(binary -> options.setBinary(Paths.get(binary)));
		options.setHeadless(configuration.isHeadless());
		Optional.ofNullable(configuration.getUserAgent()).ifPresent(ua -> options.addPreference("general.useragent.override", ua));
		Optional.ofNullable(configuration.getUserDir()).ifPresent(ud -> options.addArguments("-profile", ud));
		return options;
	}
	
	public void close(){
		try{
			log.info("Closing webdriver");
			if(driver != null){
				driver.quit();
			}
			log.info("Closed webdriver");
		}
		catch(Throwable e){
			log.error("Failed to close webdriver", e);
		}
		finally{
			selenideDriver = null;
			driver = null;
		}
	}
}
