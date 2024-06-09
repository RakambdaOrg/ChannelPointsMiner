package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
import fr.rakambda.channelpointsminer.miner.api.telegram.TelegramApi;
import fr.rakambda.channelpointsminer.miner.config.DiscordConfiguration;
import fr.rakambda.channelpointsminer.miner.config.TelegramConfiguration;
import fr.rakambda.channelpointsminer.miner.log.LoggerEventListener;
import fr.rakambda.channelpointsminer.miner.log.discord.DiscordEventListener;
import fr.rakambda.channelpointsminer.miner.log.telegram.TelegramEventListener;
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
	@Mock
	private TelegramApi telegramApi;
	@Mock
	private TelegramConfiguration telegramConfiguration;
	
	@Test
	void createLogger(){
		Assertions.assertThat(LogEventListenerFactory.createLogger()).isNotNull().isInstanceOf(LoggerEventListener.class);
	}
	
	@Test
	void createDiscordLogger(){
		Assertions.assertThat(LogEventListenerFactory.createDiscordLogger(discordApi, discordConfiguration)).isNotNull().isInstanceOf(DiscordEventListener.class);
	}
	
	@Test
	void createTelegramLogger(){
		Assertions.assertThat(LogEventListenerFactory.createTelegramLogger(telegramApi, telegramConfiguration)).isNotNull().isInstanceOf(TelegramEventListener.class);
	}
}