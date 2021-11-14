package fr.raksrinana.channelpointsminer.irc;

import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import org.kitteh.irc.client.library.Client;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
		assertThat(TwitchIrcFactory.createListener("username")).isNotNull().isInstanceOf(TwitchIrcEventListener.class);
	}
}