package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.miner.IMiner;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EventLoggerFactoryTest{
	@Mock
	private IMiner miner;
	
	@Test
	void create(){
		assertThat(EventLoggerFactory.create(miner)).isNotNull();
	}
}