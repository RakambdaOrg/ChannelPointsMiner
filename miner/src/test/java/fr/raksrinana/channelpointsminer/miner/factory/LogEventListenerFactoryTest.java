package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.miner.log.DiscordEventListener;
import fr.raksrinana.channelpointsminer.miner.log.LoggerEventListener;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class LogEventListenerFactoryTest{
	@Mock
	private DiscordApi discordApi;
	
	@Test
	void createLogger(){
		Assertions.assertThat(LogEventListenerFactory.createLogger()).isNotNull().isInstanceOf(LoggerEventListener.class);
	}
	
	@Test
	void createDiscordLogger(){
		Assertions.assertThat(LogEventListenerFactory.createDiscordLogger(discordApi, true)).isNotNull().isInstanceOf(DiscordEventListener.class);
	}
}