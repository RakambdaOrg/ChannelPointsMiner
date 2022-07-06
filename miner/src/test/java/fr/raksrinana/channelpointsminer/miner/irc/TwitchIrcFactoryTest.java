package fr.raksrinana.channelpointsminer.miner.irc;

import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.irc.listeners.TwitchIrcConnectionEventListener;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.kitteh.irc.client.library.Client;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ParallelizableTest
class TwitchIrcFactoryTest{
	@Test
	void createFromTwitchLogin(){
		var twitchLogin = mock(TwitchLogin.class);
		when(twitchLogin.getUsername()).thenReturn("user");
		when(twitchLogin.getAccessToken()).thenReturn("pass");
		
		assertThat(TwitchIrcFactory.createClient(twitchLogin)).isNotNull().isInstanceOf(Client.class);
	}
	
	@Test
	void createListener(){
		assertThat(TwitchIrcFactory.createConnectionListener("username")).isNotNull().isInstanceOf(TwitchIrcConnectionEventListener.class);
	}
}