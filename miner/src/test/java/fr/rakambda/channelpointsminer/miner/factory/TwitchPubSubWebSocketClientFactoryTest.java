package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.pubsub.TwitchPubSubWebSocketClient;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
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
		Assertions.assertThat(TwitchWebSocketClientFactory.createChatClient(twitchLogin, true)).isNotNull()
				.isInstanceOf(TwitchChatWebSocketClient.class);
	}
}