package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.handler.*;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.prediction.bet.BetPlacer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MessageHandlerFactoryTest{
	@Mock
	private IMiner miner;
	@Mock
	private BetPlacer betPlacer;
	
	@Test
	void createLogger(){
		assertThat(MessageHandlerFactory.createLogger(miner)).isNotNull().isInstanceOf(EventLoggerHandler.class);
	}
	
	@Test
	void createClaimAvailable(){
		assertThat(MessageHandlerFactory.createClaimAvailableHandler(miner)).isNotNull().isInstanceOf(ClaimAvailableHandler.class);
	}
	
	@Test
	void createStreamStartEndHandler(){
		assertThat(MessageHandlerFactory.createStreamStartEndHandler(miner)).isNotNull().isInstanceOf(StreamStartEndHandler.class);
	}
	
	@Test
	void createFollowRaidHandler(){
		assertThat(MessageHandlerFactory.createFollowRaidHandler(miner)).isNotNull().isInstanceOf(FollowRaidHandler.class);
	}
	
	@Test
	void createPredictionsHandler(){
		assertThat(MessageHandlerFactory.createPredictionsHandler(miner, betPlacer)).isNotNull().isInstanceOf(PredictionsHandler.class);
	}
}