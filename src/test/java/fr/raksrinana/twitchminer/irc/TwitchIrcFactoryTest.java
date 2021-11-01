package fr.raksrinana.twitchminer.irc;

import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
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
		assertThat(TwitchIrcFactory.createListener()).isNotNull().isInstanceOf(TwitchIrcEventListener.class);
	}
}