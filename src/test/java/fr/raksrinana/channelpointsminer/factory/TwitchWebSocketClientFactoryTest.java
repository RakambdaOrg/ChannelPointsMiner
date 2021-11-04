package fr.raksrinana.channelpointsminer.factory;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TwitchWebSocketClientFactoryTest{
	@Test
	void create(){
		assertThat(TwitchWebSocketClientFactory.createClient()).isNotNull();
	}
}