package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.log.DiscordEventListener;
import fr.raksrinana.channelpointsminer.log.LoggerEventListener;
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
		assertThat(LogEventListenerFactory.createLogger()).isNotNull().isInstanceOf(LoggerEventListener.class);
	}
	
	@Test
	void createDiscordLogger(){
		assertThat(LogEventListenerFactory.createDiscordLogger(discordApi, true)).isNotNull().isInstanceOf(DiscordEventListener.class);
	}
}