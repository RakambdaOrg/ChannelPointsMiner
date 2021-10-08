package fr.raksrinana.twitchminer.config;

import fr.raksrinana.twitchminer.cli.CLIHolder;
import fr.raksrinana.twitchminer.cli.CLIParameters;
import fr.raksrinana.twitchminer.miner.data.StreamerSettings;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationFactoryTest{
	@Mock
	private CLIParameters cliParameters;
	
	@Test
	void getInstance() throws URISyntaxException{
		var classLoader = getClass().getClassLoader();
		var resource = classLoader.getResource("config/config.json");
		var testConfig = Paths.get(resource.toURI());
		when(cliParameters.getConfigurationFile()).thenReturn(testConfig);
		
		var expected = Configuration.builder()
				.username("username")
				.password("password")
				.use2Fa(true)
				.loadFollows(true)
				.defaultStreamerSettings(StreamerSettings.builder().makePredictions(true).followRaid(true).build())
				.streamers(Set.of(
						StreamerConfiguration.builder().username("streamer1").build(),
						StreamerConfiguration.builder().username("streamer2").build()
				))
				.build();
		
		try (var cliHolder = Mockito.mockStatic(CLIHolder.class)) {
			cliHolder.when(CLIHolder::getInstance).thenReturn(cliParameters);
			
			var firstInstance = ConfigurationFactory.getInstance();
			var secondInstance = ConfigurationFactory.getInstance();
			
			assertThat(firstInstance).usingRecursiveComparison().isEqualTo(expected);
			assertThat(secondInstance).isSameAs(firstInstance);
		}
		
		ConfigurationFactory.getInstance();
	}
}