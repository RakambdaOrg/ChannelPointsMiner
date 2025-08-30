package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketClient;
import fr.rakambda.channelpointsminer.miner.api.hermes.TwitchHermesWebSocketClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.pubsub.TwitchPubSubWebSocketClient;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;

@ParallelizableTest
class TwitchPubSubWebSocketClientFactoryTest{
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private IEventManager eventManager;
	
	@Test
	void createPubSub(){
		Assertions.assertThat(TwitchWebSocketClientFactory.createPubSubClient()).isNotNull().isInstanceOf(TwitchPubSubWebSocketClient.class);
	}
	
	@Test
	void createHermesClient(){
		Assertions.assertThat(TwitchWebSocketClientFactory.createHermesClient(eventManager)).isNotNull().isInstanceOf(TwitchHermesWebSocketClient.class);
	}
	
	@Test
	void createReconnectHermesClient(){
		Assertions.assertThat(TwitchWebSocketClientFactory.createHermesClient("wss://test", eventManager)).isNotNull().isInstanceOf(TwitchHermesWebSocketClient.class);
	}
	
	@Test
	void createChat(){
		Assertions.assertThat(TwitchWebSocketClientFactory.createChatClient(twitchLogin, true)).isNotNull()
				.isInstanceOf(TwitchChatWebSocketClient.class);
	}
}