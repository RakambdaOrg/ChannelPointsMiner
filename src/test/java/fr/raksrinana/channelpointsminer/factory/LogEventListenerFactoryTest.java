package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.log.DiscordLogEventListener;
import fr.raksrinana.channelpointsminer.log.LoggerLogEventListener;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LogEventListenerFactoryTest{
	@Mock
	private DiscordApi discordApi;
	
	@Test
	void createLogger(){
		assertThat(LogEventListenerFactory.createLogger()).isNotNull().isInstanceOf(LoggerLogEventListener.class);
	}
	
	@Test
	void createDiscordLogger(){
		assertThat(LogEventListenerFactory.createDiscordLogger(discordApi, true)).isNotNull().isInstanceOf(DiscordLogEventListener.class);
	}
}