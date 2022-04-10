package fr.raksrinana.channelpointsminer.miner.factory;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TwitchWebSocketClientFactoryTest{
	@Test
	void create(){
		Assertions.assertThat(TwitchWebSocketClientFactory.createClient()).isNotNull();
	}
}