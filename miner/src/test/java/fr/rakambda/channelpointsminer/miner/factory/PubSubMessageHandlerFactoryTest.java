package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.handler.ClaimAvailableHandler;
import fr.rakambda.channelpointsminer.miner.handler.ClaimMomentHandler;
import fr.rakambda.channelpointsminer.miner.handler.FollowRaidHandler;
import fr.rakambda.channelpointsminer.miner.handler.PointsHandler;
import fr.rakambda.channelpointsminer.miner.handler.PredictionsHandler;
import fr.rakambda.channelpointsminer.miner.handler.StreamStartEndHandler;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.prediction.bet.BetPlacer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class PubSubMessageHandlerFactoryTest{
	@Mock
	private IMiner miner;
	@Mock
	private BetPlacer betPlacer;
	
	@Test
	void createClaimAvailable(){
		assertThat(PubSubMessageHandlerFactory.createClaimAvailableHandler(miner)).isNotNull().isInstanceOf(ClaimAvailableHandler.class);
	}
	
	@Test
	void createStreamStartEndHandler(){
		assertThat(PubSubMessageHandlerFactory.createStreamStartEndHandler(miner)).isNotNull().isInstanceOf(StreamStartEndHandler.class);
	}
	
	@Test
	void createFollowRaidHandler(){
		assertThat(PubSubMessageHandlerFactory.createFollowRaidHandler(miner)).isNotNull().isInstanceOf(FollowRaidHandler.class);
	}
	
	@Test
	void createPredictionsHandler(){
		assertThat(PubSubMessageHandlerFactory.createPredictionsHandler(miner, betPlacer)).isNotNull().isInstanceOf(PredictionsHandler.class);
	}
	
	@Test
	void createPointsHandler(){
		assertThat(PubSubMessageHandlerFactory.createPointsHandler(miner)).isNotNull().isInstanceOf(PointsHandler.class);
	}
	
	@Test
	void createClaimMomentHandler(){
		assertThat(PubSubMessageHandlerFactory.createClaimMomentHandler(miner)).isNotNull().isInstanceOf(ClaimMomentHandler.class);
	}
}