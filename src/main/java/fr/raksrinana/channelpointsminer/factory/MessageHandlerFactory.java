package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.handler.*;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacer;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MessageHandlerFactory{
	public static MessageHandler createLogger(@NotNull IMiner miner){
		return new EventLoggerHandler(miner);
	}
	
	public static MessageHandler createClaimAvailableHandler(@NotNull IMiner miner){
		return new ClaimAvailableHandler(miner);
	}
	
	public static MessageHandler createStreamStartEndHandler(@NotNull IMiner miner){
		return new StreamStartEndHandler(miner);
	}
	
	public static MessageHandler createFollowRaidHandler(@NotNull IMiner miner){
		return new FollowRaidHandler(miner);
	}
	
	public static MessageHandler createPredictionsHandler(@NotNull IMiner miner, @NotNull BetPlacer betPlacer){
		return new fr.raksrinana.channelpointsminer.handler.PredictionsHandler(miner, betPlacer);
	}
}
