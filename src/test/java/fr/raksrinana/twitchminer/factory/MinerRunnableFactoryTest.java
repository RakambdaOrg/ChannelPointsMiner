package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.miner.IMiner;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MinerRunnableFactoryTest{
	@Mock
	private IMiner miner;
	
	@Test
	void getUpdateStreamInfo(){
		assertThat(MinerRunnableFactory.createUpdateStreamInfo(miner)).isNotNull();
	}
	
	@Test
	void getSendMinutesWatched(){
		assertThat(MinerRunnableFactory.createSendMinutesWatched(miner)).isNotNull();
	}
	
	@Test
	void getWebSocketPing(){
		assertThat(MinerRunnableFactory.createWebSocketPing(miner)).isNotNull();
	}
}