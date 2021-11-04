package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.runnable.SendMinutesWatched;
import fr.raksrinana.channelpointsminer.runnable.SyncInventory;
import fr.raksrinana.channelpointsminer.runnable.UpdateStreamInfo;
import fr.raksrinana.channelpointsminer.runnable.WebSocketPing;
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
}