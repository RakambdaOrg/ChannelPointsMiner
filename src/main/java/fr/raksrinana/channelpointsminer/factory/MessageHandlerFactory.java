package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.handler.*;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacer;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MessageHandlerFactory{
	@NotNull
	public static IMessageHandler createClaimAvailableHandler(@NotNull IMiner miner){
		return new ClaimAvailableHandler(miner);
	}
	
	@NotNull
	public static IMessageHandler createStreamStartEndHandler(@NotNull IMiner miner){
		return new StreamStartEndHandler(miner);
	}
	
	@NotNull
	public static IMessageHandler createFollowRaidHandler(@NotNull IMiner miner){
		return new FollowRaidHandler(miner);
	}
	
	@NotNull
	public static IMessageHandler createPredictionsHandler(@NotNull IMiner miner, @NotNull BetPlacer betPlacer){
		return new PredictionsHandler(miner, betPlacer);
	}
	
	@NotNull
	public static IMessageHandler createPointsHandler(@NotNull IMiner miner){
		return new PointsHandler(miner);
	}
}
