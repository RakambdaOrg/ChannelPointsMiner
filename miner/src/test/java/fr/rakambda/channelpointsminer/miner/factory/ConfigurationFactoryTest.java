package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.cli.CLIHolder;
import fr.rakambda.channelpointsminer.miner.cli.CLIParameters;
import fr.rakambda.channelpointsminer.miner.config.AccountConfiguration;
import fr.rakambda.channelpointsminer.miner.config.AnalyticsConfiguration;
import fr.rakambda.channelpointsminer.miner.config.BrowserDriver;
import fr.rakambda.channelpointsminer.miner.config.ChatMode;
import fr.rakambda.channelpointsminer.miner.config.Configuration;
import fr.rakambda.channelpointsminer.miner.config.DatabaseConfiguration;
import fr.rakambda.channelpointsminer.miner.config.DiscordConfiguration;
import fr.rakambda.channelpointsminer.miner.config.StreamerDirectory;
import fr.rakambda.channelpointsminer.miner.config.VersionProvider;
import fr.rakambda.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.rakambda.channelpointsminer.miner.config.login.HttpLoginMethod;
import fr.rakambda.channelpointsminer.miner.streamer.StreamerSettings;
import fr.rakambda.channelpointsminer.miner.tests.TestUtils;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationFactoryTest{
	@Mock
	private CLIParameters cliParameters;
	
	@BeforeEach
	void setUp(){
		ConfigurationFactory.resetInstance();
	}
	
	@Test
	void getInstanceDefaults() throws MalformedURLException{
		var testConfig = TestUtils.getResourcePath("config/config-minimalistic.json");
		when(cliParameters.getConfigurationFile()).thenReturn(testConfig);
		
		var expected = Configuration.builder()
				.accounts(List.of(AccountConfiguration.builder()
						.username("username")
						.loginMethod(HttpLoginMethod.builder()
								.password("password")
								.use2Fa(false)
								.build())
						.loadFollows(false)
						.enabled(true)
						.defaultStreamerSettings(StreamerSettings.builder()
								.makePredictions(false)
								.followRaid(false)
								.participateCampaigns(false)
								.build())
						.streamerConfigDirectories(List.of(StreamerDirectory.builder()
								.path(Paths.get("streamers"))
								.recursive(false)
								.build()))
						.discord(DiscordConfiguration.builder()
								.url(new URL("https://discord-webhook"))
								.embeds(false)
								.build())
						.reloadEvery(15)
						.analytics(AnalyticsConfiguration.builder()
								.enabled(false)
								.build())
						.build()))
				.build();
		
		try(var cliHolder = Mockito.mockStatic(CLIHolder.class)){
			cliHolder.when(CLIHolder::getInstance).thenReturn(cliParameters);
			
			var firstInstance = ConfigurationFactory.getInstance();
			var secondInstance = ConfigurationFactory.getInstance();
			
			Assertions.assertThat(firstInstance).usingRecursiveComparison().isEqualTo(expected);
			Assertions.assertThat(secondInstance).isSameAs(firstInstance);
		}
	}
	
	@Test
	void getInstanceOverridden() throws MalformedURLException{
		var testConfig = TestUtils.getResourcePath("config/config-with-more-customization.json");
		when(cliParameters.getConfigurationFile()).thenReturn(testConfig);
		
		var expected = Configuration.builder()
				.accounts(List.of(AccountConfiguration.builder()
						.username("username")
						.loginMethod(BrowserConfiguration.builder()
								.driver(BrowserDriver.REMOTE_CHROME)
								.remoteHost("http://selenium-hub:4444/wd/hub")
								.userDir("/home/seluser/profiles/channelpointsminer")
								.cookiesPath("/path/to/cookies.json")
								.build())
						.loadFollows(true)
						.enabled(false)
						.defaultStreamerSettings(StreamerSettings.builder()
								.makePredictions(true)
								.followRaid(true)
								.participateCampaigns(true)
								.build())
						.streamerConfigDirectories(List.of(StreamerDirectory.builder()
								.path(Paths.get("streamers"))
								.recursive(true)
								.build()))
						.discord(DiscordConfiguration.builder()
								.url(new URL("https://discord-webhook"))
								.embeds(true)
								.build())
						.reloadEvery(15)
						.analytics(AnalyticsConfiguration.builder()
								.enabled(true)
								.recordChatsPredictions(true)
								.database(DatabaseConfiguration.builder()
										.jdbcUrl("jdbcUrl")
										.username("user")
										.password("pass")
										.maxPoolSize(15)
										.build())
								.build())
						.chatMode(ChatMode.IRC)
						.versionProvider(VersionProvider.MANIFEST)
						.build()))
				.build();
		
		try(var cliHolder = Mockito.mockStatic(CLIHolder.class)){
			cliHolder.when(CLIHolder::getInstance).thenReturn(cliParameters);
			
			var firstInstance = ConfigurationFactory.getInstance();
			var secondInstance = ConfigurationFactory.getInstance();
			
			Assertions.assertThat(firstInstance).usingRecursiveComparison().isEqualTo(expected);
			Assertions.assertThat(secondInstance).isSameAs(firstInstance);
		}
	}
	
	@Test
	void noFile(){
		var testConfig = Paths.get("fake/file.json");
		when(cliParameters.getConfigurationFile()).thenReturn(testConfig);
		
		try(var cliHolder = Mockito.mockStatic(CLIHolder.class)){
			cliHolder.when(CLIHolder::getInstance).thenReturn(cliParameters);
			
			assertThrows(IllegalStateException.class, ConfigurationFactory::getInstance);
		}
	}
}