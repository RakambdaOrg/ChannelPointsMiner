package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.handler.ClaimAvailableHandler;
import fr.raksrinana.channelpointsminer.handler.FollowRaidHandler;
import fr.raksrinana.channelpointsminer.handler.PredictionsHandler;
import fr.raksrinana.channelpointsminer.handler.StreamStartEndHandler;
import fr.raksrinana.channelpointsminer.log.DiscordLoggerHandler;
import fr.raksrinana.channelpointsminer.log.LogLoggerHandler;
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
	@Mock
	private DiscordApi discordApi;
	
	@Test
	void createLogger(){
		assertThat(MessageHandlerFactory.createLogger(miner)).isNotNull().isInstanceOf(LogLoggerHandler.class);
	}
	
	@Test
	void createDiscordLogger(){
		assertThat(MessageHandlerFactory.createDiscordLogger(miner, discordApi)).isNotNull().isInstanceOf(DiscordLoggerHandler.class);
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