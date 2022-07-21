package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketClient;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.ws.TwitchPubSubWebSocketClient;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;

@ParallelizableTest
class TwitchPubSubWebSocketClientFactoryTest{
	@Mock
	private TwitchLogin twitchLogin;
	
	@Test
	void createPubSub(){
		Assertions.assertThat(TwitchWebSocketClientFactory.createPubSubClient()).isNotNull().isInstanceOf(TwitchPubSubWebSocketClient.class);
	}
	
	@Test
	void createChat(){
		Assertions.assertThat(TwitchWebSocketClientFactory.createChatClient(twitchLogin)).isNotNull().isInstanceOf(TwitchChatWebSocketClient.class);
	}
}