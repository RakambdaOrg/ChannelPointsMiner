package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
import fr.rakambda.channelpointsminer.miner.config.DiscordConfiguration;
import fr.rakambda.channelpointsminer.miner.log.LoggerEventListener;
import fr.rakambda.channelpointsminer.miner.log.discord.DiscordEventListener;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
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
	@Mock
	private DiscordConfiguration discordConfiguration;
	
	@Test
	void createLogger(){
		Assertions.assertThat(LogEventListenerFactory.createLogger()).isNotNull().isInstanceOf(LoggerEventListener.class);
	}
	
	@Test
	void createDiscordLogger(){
		Assertions.assertThat(LogEventListenerFactory.createDiscordLogger(discordApi, discordConfiguration)).isNotNull().isInstanceOf(DiscordEventListener.class);
	}
}