package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.runnable.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MinerRunnableFactoryTest{
	@Mock
	private IMiner miner;
	@Mock
	private StreamerSettingsFactory streamerSettingsFactory;
	
	@Test
	void createUpdateStreamInfo(){
		assertThat(MinerRunnableFactory.createUpdateStreamInfo(miner)).isNotNull()
				.isInstanceOf(UpdateStreamInfo.class);
	}
	
	@Test
	void createSendMinutesWatched(){
		assertThat(MinerRunnableFactory.createSendMinutesWatched(miner)).isNotNull()
				.isInstanceOf(SendMinutesWatched.class);
	}
	
	@Test
	void createWebSocketPing(){
		assertThat(MinerRunnableFactory.createWebSocketPing(miner)).isNotNull()
				.isInstanceOf(WebSocketPing.class);
	}
	
	@Test
	void createSyncInventory(){
		assertThat(MinerRunnableFactory.createSyncInventory(miner)).isNotNull()
				.isInstanceOf(SyncInventory.class);
	}
	
	@Test
	void createStreamerConfigurationReload(){
		assertThat(MinerRunnableFactory.createStreamerConfigurationReload(miner, streamerSettingsFactory, false)).isNotNull()
				.isInstanceOf(StreamerConfigurationReload.class);
	}
}