package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.cli.CLIHolder;
import fr.raksrinana.channelpointsminer.cli.CLIParameters;
import fr.raksrinana.channelpointsminer.config.Configuration;
import fr.raksrinana.channelpointsminer.streamer.StreamerSettings;
import fr.raksrinana.channelpointsminer.tests.TestUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;
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
	void getInstance(){
		var testConfig = TestUtils.getResourcePath("config/config.json");
		when(cliParameters.getConfigurationFile()).thenReturn(testConfig);
		
		var expected = Configuration.builder()
				.username("username")
				.password("password")
				.use2Fa(true)
				.loadFollows(true)
				.defaultStreamerSettings(StreamerSettings.builder()
						.makePredictions(true)
						.followRaid(true)
						.participateCampaigns(true)
						.build())
				.build();
		
		try(var cliHolder = Mockito.mockStatic(CLIHolder.class)){
			cliHolder.when(CLIHolder::getInstance).thenReturn(cliParameters);
			
			var firstInstance = ConfigurationFactory.getInstance();
			var secondInstance = ConfigurationFactory.getInstance();
			
			assertThat(firstInstance).usingRecursiveComparison().isEqualTo(expected);
			assertThat(secondInstance).isSameAs(firstInstance);
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