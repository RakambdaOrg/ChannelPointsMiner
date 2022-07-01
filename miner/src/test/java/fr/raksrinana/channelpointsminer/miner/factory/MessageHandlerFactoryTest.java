package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.handler.ClaimAvailableHandler;
import fr.raksrinana.channelpointsminer.miner.handler.FollowRaidHandler;
import fr.raksrinana.channelpointsminer.miner.handler.PointsHandler;
import fr.raksrinana.channelpointsminer.miner.handler.PredictionsHandler;
import fr.raksrinana.channelpointsminer.miner.handler.StreamStartEndHandler;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.BetPlacer;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
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