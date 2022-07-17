package fr.raksrinana.channelpointsminer.miner.api.chat;

import fr.raksrinana.channelpointsminer.miner.api.chat.irc.TwitchIrcChatClient;
import fr.raksrinana.channelpointsminer.miner.api.chat.irc.TwitchIrcEventListener;
import fr.raksrinana.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.config.ChatMode;
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
class TwitchChatFactoryTest{
	@Mock
	private TwitchLogin twitchLogin;
	
	@Test
	void createFromTwitchLogin(){
		when(twitchLogin.getUsername()).thenReturn("user");
		when(twitchLogin.getAccessToken()).thenReturn("pass");
		
		assertThat(TwitchChatFactory.createIrcClient(twitchLogin)).isNotNull().isInstanceOf(Client.class);
	}
	
	@Test
	void createListener(){
		assertThat(TwitchChatFactory.createIrcListener("username")).isNotNull().isInstanceOf(TwitchIrcEventListener.class);
	}
	
	@Test
	void createIrcChat(){
		assertThat(TwitchChatFactory.createChat(ChatMode.IRC, twitchLogin)).isNotNull().isInstanceOf(TwitchIrcChatClient.class);
	}
	
	@Test
	void createWsChat(){
		assertThat(TwitchChatFactory.createChat(ChatMode.WS, twitchLogin)).isNotNull().isInstanceOf(TwitchChatWebSocketPool.class);
	}
}