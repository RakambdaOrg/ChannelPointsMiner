package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatMessageListener;
import fr.raksrinana.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketClient;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.ws.TwitchPubSubWebSocketClient;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@ParallelizableTest
class TwitchPubSubWebSocketClientFactoryTest{
	@Mock
	private TwitchLogin twitchLogin;
    private final List<ITwitchChatMessageListener> chatMessageListeners = new LinkedList<>();
    
	@Test
	void createPubSub(){
		Assertions.assertThat(TwitchWebSocketClientFactory.createPubSubClient()).isNotNull().isInstanceOf(TwitchPubSubWebSocketClient.class);
	}
	
	@Test
	void createChat(){
		Assertions.assertThat(TwitchWebSocketClientFactory.createChatClient(twitchLogin, chatMessageListeners)).isNotNull().isInstanceOf(TwitchChatWebSocketClient.class);
	}
}