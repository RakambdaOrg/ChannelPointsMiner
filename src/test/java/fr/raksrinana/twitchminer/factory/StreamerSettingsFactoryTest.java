package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.config.Configuration;
import fr.raksrinana.twitchminer.miner.data.StreamerSettings;
import fr.raksrinana.twitchminer.tests.TestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreamerSettingsFactoryTest{
	private static final StreamerSettings DEFAULT = StreamerSettings.builder().build();
	private static final String STREAMER_USERNAME = "streamer-name";
	
	@TempDir
	private Path tempDir;
	
	@InjectMocks
	private StreamerSettingsFactory tested;
	
	@Mock
	private Configuration configuration;
	
	@BeforeEach
	void setUp(){
		when(configuration.getDefaultStreamerSettings()).thenReturn(DEFAULT);
		lenient().when(configuration.getStreamerConfigDirectory()).thenReturn(tempDir);
	}
	
	@Test
	void getDefaultConfiguration(){
		assertThat(tested.getDefaultSettings()).isSameAs(DEFAULT);
	}
	
	@Test
	void getStreamerConfigurationWithConfigFolderMissing(){
		when(configuration.getStreamerConfigDirectory()).thenReturn(tempDir.resolve("unknown-dir"));
		
		assertThat(tested.createStreamerSettings(STREAMER_USERNAME)).isNotSameAs(DEFAULT)
				.usingRecursiveComparison().isEqualTo(DEFAULT);
	}
	
	@Test
	void getStreamerConfigurationWithNoConfigFile(){
		assertThat(tested.createStreamerSettings(STREAMER_USERNAME)).isNotSameAs(DEFAULT)
				.usingRecursiveComparison().isEqualTo(DEFAULT);
	}
	
	@Test
	void getStreamerConfigurationWithConfigFileEmpty(){
		TestUtils.copyFromResources("factory/empty.json", tempDir.resolve(STREAMER_USERNAME + ".json"));
		
		assertThat(tested.createStreamerSettings(STREAMER_USERNAME)).isNotSameAs(DEFAULT)
				.usingRecursiveComparison().isEqualTo(DEFAULT);
	}
	
	@Test
	void getStreamerConfigurationWithConfigFileNothingRedefined(){
		TestUtils.copyFromResources("factory/nothingRedefined.json", tempDir.resolve(STREAMER_USERNAME + ".json"));
		
		assertThat(tested.createStreamerSettings(STREAMER_USERNAME)).isNotSameAs(DEFAULT)
				.usingRecursiveComparison().isEqualTo(DEFAULT);
	}
	
	@Test
	void getStreamerConfigurationWithConfigFilePartiallyRedefined(){
		TestUtils.copyFromResources("factory/partiallyOverridden.json", tempDir.resolve(STREAMER_USERNAME + ".json"));
		
		assertThat(tested.createStreamerSettings(STREAMER_USERNAME)).isNotSameAs(DEFAULT)
				.usingRecursiveComparison().isEqualTo(StreamerSettings.builder()
						.followRaid(true)
						.build());
	}
	
	@Test
	void getStreamerConfigurationWithConfigFileAllRedefined(){
		TestUtils.copyFromResources("factory/fullyOverridden.json", tempDir.resolve(STREAMER_USERNAME + ".json"));
		
		assertThat(tested.createStreamerSettings(STREAMER_USERNAME)).isNotSameAs(DEFAULT)
				.usingRecursiveComparison().isEqualTo(StreamerSettings.builder()
						.makePredictions(true)
						.followRaid(true)
						.build());
	}
}