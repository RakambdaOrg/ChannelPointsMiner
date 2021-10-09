package fr.raksrinana.twitchminer.miner.data;

import fr.raksrinana.twitchminer.config.Configuration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreamerSettingsFactoryTest{
	private static final StreamerSettings DEFAULT = StreamerSettings.builder().build();
	
	@InjectMocks
	private StreamerSettingsFactory tested;
	
	@Mock
	private Configuration configuration;
	
	@BeforeEach
	void setUp(){
		when(configuration.getDefaultStreamerSettings()).thenReturn(DEFAULT);
	}
	
	@Test
	void getDefaultConfiguration(){
		assertThat(tested.getDefaultSettings()).isSameAs(DEFAULT);
	}
	
	@Test
	void getStreamerConfiguration(){
		assertThat(tested.readStreamerSettings()).isNotSameAs(DEFAULT)
				.usingRecursiveComparison().isEqualTo(DEFAULT);
	}
}