package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.handler.ClaimAvailableHandler;
import fr.rakambda.channelpointsminer.miner.handler.ClaimMomentHandler;
import fr.rakambda.channelpointsminer.miner.handler.FollowRaidHandler;
import fr.rakambda.channelpointsminer.miner.handler.IPubSubMessageHandler;
import fr.rakambda.channelpointsminer.miner.handler.PointsHandler;
import fr.rakambda.channelpointsminer.miner.handler.PredictionsHandler;
import fr.rakambda.channelpointsminer.miner.handler.StreamStartEndHandler;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.prediction.bet.BetPlacer;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PubSubMessageHandlerFactory{
	@NotNull
	public static IPubSubMessageHandler createClaimAvailableHandler(@NotNull IMiner miner){
		return new ClaimAvailableHandler(miner);
	}
	
	@NotNull
	public static IPubSubMessageHandler createStreamStartEndHandler(@NotNull IMiner miner){
		return new StreamStartEndHandler(miner);
	}
	
	@NotNull
	public static IPubSubMessageHandler createFollowRaidHandler(@NotNull IMiner miner){
		return new FollowRaidHandler(miner);
	}
	
	@NotNull
	public static IPubSubMessageHandler createPredictionsHandler(@NotNull IMiner miner, @NotNull BetPlacer betPlacer){
		return new PredictionsHandler(miner, betPlacer);
	}
	
	@NotNull
	public static IPubSubMessageHandler createPointsHandler(@NotNull IMiner miner){
		return new PointsHandler(miner);
	}
	
	@NotNull
	public static IPubSubMessageHandler createClaimMomentHandler(@NotNull IMiner miner){
		return new ClaimMomentHandler(miner);
	}
}
