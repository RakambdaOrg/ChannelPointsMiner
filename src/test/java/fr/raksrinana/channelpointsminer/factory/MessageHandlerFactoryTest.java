package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.handler.*;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacer;
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
	
	@Test
	void createPointsHandler(){
		assertThat(MessageHandlerFactory.createPointsHandler(miner)).isNotNull().isInstanceOf(PointsHandler.class);
	}
}