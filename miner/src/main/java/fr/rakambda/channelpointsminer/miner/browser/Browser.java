package fr.rakambda.channelpointsminer.miner.browser;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import fr.rakambda.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v140.network.Network;
import org.openqa.selenium.devtools.v140.network.model.RequestId;
import org.openqa.selenium.devtools.v140.network.model.RequestWillBeSent;
import org.openqa.selenium.devtools.v140.network.model.ResponseReceived;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import static java.util.Optional.empty;

@Log4j2
@RequiredArgsConstructor
public class Browser implements AutoCloseable{
	@NonNull
	private final BrowserConfiguration browserConfiguration;
	@NonNull
	private final IEventManager eventManager;
	
	@Getter
	private WebDriver driver;
	private DevTools devTools;
	private SelenideDriver selenideDriver;
	@Getter
	private final Collection<RequestWillBeSent> sentRequests = new ConcurrentLinkedQueue<>();
	@Getter
	private final Collection<ResponseReceived> receivedResponses = new ConcurrentLinkedQueue<>();
	
	@NonNull
	public BrowserController setup(){
		log.info("Starting browser...");
		
		var config = setupSelenideConfig(new SelenideConfig());
		driver = buildDriver(browserConfiguration);
		driver = new Augmenter().augment(driver);
		
		if(!(driver instanceof HasDevTools devToolsDriver)){
			throw new IllegalStateException("Browser must have dev tools support");
		}
		
		driver.manage().window().maximize();
		
		devTools = devToolsDriver.maybeGetDevTools().orElseThrow(() -> new IllegalStateException("Failed to get devTools"));
		devTools.createSession();
		setupHideJsElements(devTools);
		listenNetwork(devTools);
		
		selenideDriver = new SelenideDriver(config, driver, null);
		return new BrowserController(selenideDriver, eventManager);
	}
	
	private void setupHideJsElements(@NonNull DevTools devTools){
		devTools.send(new Command<>("Page.addScriptToEvaluateOnNewDocument", Map.of("source", """
				Object.defineProperty(navigator, 'webdriver', {
					get: () => undefined
				})"""
		)));
		devTools.send(new Command<>("Page.addScriptToEvaluateOnNewDocument", Map.of("source", """
				let objectToInspect = window;
				let result = [];
				while(objectToInspect !== null)
				{
					result = result.concat(Object.getOwnPropertyNames(objectToInspect));
				    objectToInspect = Object.getPrototypeOf(objectToInspect);
				}
				result.forEach(p => p.match(/.+_.+_(Array|Promise|Symbol)/ig)
									&& delete window[p]
									&& console.log('removed', p));"""
		)));
	}
	
	private void listenNetwork(@NonNull DevTools devTools){
		devTools.send(Network.enable(empty(), empty(), empty(), empty()));
		devTools.addListener(Network.requestWillBeSent(), sentRequests::add);
		devTools.addListener(Network.responseReceived(), receivedResponses::add);
	}
	
	@NonNull
	public String getRequestBody(@NonNull RequestId requestId){
		return devTools.send(Network.getResponseBody(requestId)).getBody();
	}
	
	@NonNull
	private SelenideConfig setupSelenideConfig(@NonNull SelenideConfig config){
		config.savePageSource(false);
		config.screenshots(browserConfiguration.isScreenshots());
		config.headless(browserConfiguration.isHeadless());
		return config;
	}
	
	@NonNull
	private WebDriver buildDriver(@NonNull BrowserConfiguration config){
		return switch(config.getDriver()){
			case CHROME -> getChromeDriver(config);
			case FIREFOX -> getFirefoxDriver(config);
			case REMOTE_CHROME -> getRemoteDriverChrome(config);
			case REMOTE_FIREFOX -> getRemoteDriverFirefox(config);
		};
	}
	
	@NonNull
	private ChromeDriver getChromeDriver(@NonNull BrowserConfiguration configuration){
		var options = getDefaultChromeOptions(configuration);
		Optional.ofNullable(configuration.getBinary()).ifPresent(binary -> options.setBinary(Paths.get(binary).toFile()));
		return new ChromeDriver(options);
	}
	
	@NonNull
	private FirefoxDriver getFirefoxDriver(@NonNull BrowserConfiguration configuration){
		var options = getDefaultFirefoxOptions(configuration);
		Optional.ofNullable(configuration.getBinary()).ifPresent(binary -> options.setBinary(Paths.get(binary)));
		return new FirefoxDriver(options);
	}
	
	@SneakyThrows
	@NonNull
	private RemoteWebDriver getRemoteDriverChrome(@NonNull BrowserConfiguration configuration){
		return new RemoteWebDriver(URI.create(configuration.getRemoteHost()).toURL(), getDefaultChromeOptions(configuration));
	}
	
	@SneakyThrows
	@NonNull
	private RemoteWebDriver getRemoteDriverFirefox(@NonNull BrowserConfiguration configuration){
		return new RemoteWebDriver(URI.create(configuration.getRemoteHost()).toURL(), getDefaultFirefoxOptions(configuration));
	}
	
	@NonNull
	private ChromeOptions getDefaultChromeOptions(@NonNull BrowserConfiguration configuration){
		var options = new ChromeOptions();
		if(configuration.isHeadless()){
			options.addArguments("--headless=new");
		}
		Optional.ofNullable(configuration.getUserAgent()).map("user-agent=\"%s\""::formatted).ifPresent(options::addArguments);
		Optional.ofNullable(configuration.getUserDir()).map(ud -> ud.replace(" ", "\\ ")).map("user-data-dir=%s"::formatted).ifPresent(options::addArguments);
		
		options.addArguments("--disable-blink-features=AutomationControlled");
		options.addArguments("--no-sandbox");
		if(configuration.isDisableShm()){
			options.addArguments("--disable-dev-shm-usage");
		}
		
		options.addArguments("disable-infobars");
		options.addArguments("disable-popup-blocking");
		
		options.setExperimentalOption("excludeSwitches", Set.of("enable-automation"));
		options.setExperimentalOption("useAutomationExtension", false);
		
		var loggingPreferences = new LoggingPreferences();
		loggingPreferences.enable(LogType.BROWSER, Level.WARNING);
		loggingPreferences.enable(LogType.PERFORMANCE, Level.WARNING);
		loggingPreferences.enable(LogType.PROFILER, Level.WARNING);
		options.setCapability(ChromeOptions.LOGGING_PREFS, loggingPreferences);
		
		return options;
	}
	
	@NonNull
	private FirefoxOptions getDefaultFirefoxOptions(@NonNull BrowserConfiguration configuration){
		var profile = new FirefoxProfile();
		profile.setPreference("dom.webdriver.enabled", false);
		profile.setPreference("useAutomationExtension", false);
		Optional.ofNullable(configuration.getUserDir()).ifPresent(dir -> profile.setPreference("profile", dir));
		
		var options = new FirefoxOptions();
		options.setProfile(profile);
		if(configuration.isHeadless()){
			options.addArguments("-headless");
		}
		Optional.ofNullable(configuration.getUserAgent()).ifPresent(ua -> options.addPreference("general.useragent.override", ua));
		
		return options;
	}
	
	public void close(){
		try{
			log.info("Closing webdriver");
			if(devTools != null){
				devTools.disconnectSession();
				log.debug("Closed dev tools");
			}
			if(driver != null){
				driver.quit();
				CommonUtils.randomSleep(2000, 1);
			}
			log.info("Closed webdriver");
		}
		catch(Throwable e){
			log.error("Failed to close webdriver", e);
		}
		finally{
			selenideDriver = null;
			devTools = null;
			driver = null;
		}
	}
}
