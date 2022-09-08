package fr.raksrinana.channelpointsminer.miner.api.chat.irc;

import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.kitteh.irc.client.library.Client;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class TwitchIrcFactoryTest{
	@Mock
	private TwitchLogin twitchLogin;
	
	@Test
	void createFromTwitchLogin(){
		when(twitchLogin.getUsername()).thenReturn("user");
		when(twitchLogin.getAccessToken()).thenReturn("pass");
		
		assertThat(TwitchIrcFactory.createIrcClient(twitchLogin)).isNotNull().isInstanceOf(Client.class);
	}
	
	@Test
	void createIrcConnectionHandler(){
		assertThat(TwitchIrcFactory.createIrcConnectionHandler("username")).isNotNull()
				.isInstanceOf(TwitchIrcConnectionHandler.class);
	}
	
	@Test
	void createIrcMessageHandler(){
		assertThat(TwitchIrcFactory.createIrcMessageHandler("username")).isNotNull()
				.isInstanceOf(TwitchIrcMessageHandler.class);
	}
}