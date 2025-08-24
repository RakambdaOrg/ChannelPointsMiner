package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.runnable.HermesWebSocketPing;
import fr.rakambda.channelpointsminer.miner.runnable.SendM3u8MinutesWatched;
import fr.rakambda.channelpointsminer.miner.runnable.SendSpadeMinutesWatched;
import fr.rakambda.channelpointsminer.miner.runnable.StreamerConfigurationReload;
import fr.rakambda.channelpointsminer.miner.runnable.SyncInventory;
import fr.rakambda.channelpointsminer.miner.runnable.UpdateStreamInfo;
import fr.rakambda.channelpointsminer.miner.runnable.ChatWebSocketPing;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class MinerRunnableFactoryTest{
	@Mock
	private IMiner miner;
	@Mock
	private StreamerSettingsFactory streamerSettingsFactory;
	@Mock
	private IEventManager eventManager;
	
	@Test
	void createUpdateStreamInfo(){
		Assertions.assertThat(MinerRunnableFactory.createUpdateStreamInfo(miner)).isNotNull()
				.isInstanceOf(UpdateStreamInfo.class);
	}
	
	@Test
	void createSendM3u8MinutesWatched(){
		Assertions.assertThat(MinerRunnableFactory.createSendM3u8MinutesWatched(miner)).isNotNull()
				.isInstanceOf(SendM3u8MinutesWatched.class);
	}
	
	@Test
	void createSendSpadeMinutesWatched(){
		Assertions.assertThat(MinerRunnableFactory.createSendSpadeMinutesWatched(miner)).isNotNull()
				.isInstanceOf(SendSpadeMinutesWatched.class);
	}
	
	@Test
	void createChatWebSocketPing(){
		Assertions.assertThat(MinerRunnableFactory.createChatWebSocketPing(miner)).isNotNull()
				.isInstanceOf(ChatWebSocketPing.class);
	}
	
	@Test
	void createHermesWebSocketPing(){
		Assertions.assertThat(MinerRunnableFactory.createHermesWebSocketPing(miner)).isNotNull()
				.isInstanceOf(HermesWebSocketPing.class);
	}
	
	@Test
	void createSyncInventory(){
		Assertions.assertThat(MinerRunnableFactory.createSyncInventory(miner, eventManager)).isNotNull()
				.isInstanceOf(SyncInventory.class);
	}
	
	@Test
	void createStreamerConfigurationReload(){
		Assertions.assertThat(MinerRunnableFactory.createStreamerConfigurationReload(miner, eventManager, streamerSettingsFactory, false)).isNotNull()
				.isInstanceOf(StreamerConfigurationReload.class);
	}
}