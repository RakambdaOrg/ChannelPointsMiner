package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.miner.IMiner;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MessageHandlerFactoryTest{
	@Mock
	private IMiner miner;
	
	@Test
	void createLogger(){
		assertThat(MessageHandlerFactory.createLogger(miner)).isNotNull();
	}
	
	@Test
	void createClaimAvailable(){
		assertThat(MessageHandlerFactory.createClaimAvailableHandler(miner)).isNotNull();
	}
	
	@Test
	void createStreamStartEndHandler(){
		assertThat(MessageHandlerFactory.createStreamStartEndHandler(miner)).isNotNull();
	}
	
	@Test
	void createFollowRaidHandler(){
		assertThat(MessageHandlerFactory.createFollowRaidHandler(miner)).isNotNull();
	}
}