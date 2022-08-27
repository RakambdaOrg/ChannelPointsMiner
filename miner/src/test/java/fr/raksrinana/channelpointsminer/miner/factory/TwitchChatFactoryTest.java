package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.chat.irc.TwitchIrcChatClient;
import fr.raksrinana.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.config.ChatMode;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class TwitchChatFactoryTest{
	
	@Mock
	private IMiner miner;
	@Mock
	private TwitchLogin twitchLogin;
	
	@BeforeEach
	void setUp(){
		when(miner.getTwitchLogin()).thenReturn(twitchLogin);
	}
	
	@Test
	void createIrcChat(){
		assertThat(TwitchChatFactory.createChat(miner, ChatMode.IRC, false)).isNotNull()
				.isInstanceOf(TwitchIrcChatClient.class);
	}
	
	@Test
	void createWsChat(){
		assertThat(TwitchChatFactory.createChat(miner, ChatMode.WS, false)).isNotNull().isInstanceOf(TwitchChatWebSocketPool.class);
	}
}