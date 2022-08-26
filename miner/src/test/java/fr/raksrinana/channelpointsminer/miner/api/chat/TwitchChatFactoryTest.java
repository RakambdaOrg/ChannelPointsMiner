package fr.raksrinana.channelpointsminer.miner.api.chat;

import fr.raksrinana.channelpointsminer.miner.api.chat.irc.TwitchIrcChatClient;
import fr.raksrinana.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.config.ChatMode;
import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class TwitchChatFactoryTest{

    @Mock
	private TwitchLogin twitchLogin;
    @Mock
    private IDatabase database;
	
	@Test
	void createIrcChatWithNoPredictionRecording(){
        TwitchChatFactory tested = new TwitchChatFactory();
		assertThat(tested.createChat(ChatMode.IRC, twitchLogin)).isNotNull().isInstanceOf(TwitchIrcChatClient.class);
	}
	
	@Test
	void createWsChatWithNoPredictionRecording(){
        TwitchChatFactory tested = new TwitchChatFactory();
		assertThat(tested.createChat(ChatMode.WS, twitchLogin)).isNotNull().isInstanceOf(TwitchChatWebSocketPool.class);
	}
    
    @Test
    void createIrcChatWithListenersPredictionRecording(){
        TwitchChatFactory tested = new TwitchChatFactory(database);
        assertThat(tested.createChat(ChatMode.IRC, twitchLogin)).isNotNull().isInstanceOf(TwitchIrcChatClient.class);
    }
    
    @Test
    void createWsChatWithListenersPredictionRecording(){
        TwitchChatFactory tested = new TwitchChatFactory(database);
        assertThat(tested.createChat(ChatMode.WS, twitchLogin)).isNotNull().isInstanceOf(TwitchChatWebSocketPool.class);
    }
}