package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.handler.ClaimAvailableHandler;
import fr.rakambda.channelpointsminer.miner.handler.ClaimDropHandler;
import fr.rakambda.channelpointsminer.miner.handler.ClaimMomentHandler;
import fr.rakambda.channelpointsminer.miner.handler.FollowRaidHandler;
import fr.rakambda.channelpointsminer.miner.handler.IPubSubMessageHandler;
import fr.rakambda.channelpointsminer.miner.handler.PointsHandler;
import fr.rakambda.channelpointsminer.miner.handler.PredictionsHandler;
import fr.rakambda.channelpointsminer.miner.handler.StreamStartEndHandler;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.prediction.bet.BetPlacer;
import fr.rakambda.channelpointsminer.miner.runnable.SyncInventory;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PubSubMessageHandlerFactory{
	@NotNull
	public static IPubSubMessageHandler createClaimAvailableHandler(@NotNull IMiner miner, @NotNull IEventManager eventManager){
		return new ClaimAvailableHandler(miner, eventManager);
	}
	
	@NotNull
	public static IPubSubMessageHandler createStreamStartEndHandler(@NotNull IMiner miner, @NotNull IEventManager eventManager){
		return new StreamStartEndHandler(miner, eventManager);
	}
	
	@NotNull
	public static IPubSubMessageHandler createFollowRaidHandler(@NotNull IMiner miner){
		return new FollowRaidHandler(miner);
	}
	
	@NotNull
	public static IPubSubMessageHandler createPredictionsHandler(@NotNull IMiner miner, @NotNull BetPlacer betPlacer, @NotNull IEventManager eventManager){
		return new PredictionsHandler(miner, betPlacer, eventManager);
	}
	
	@NotNull
	public static IPubSubMessageHandler createPointsHandler(@NotNull IMiner miner, @NotNull IEventManager eventManager){
		return new PointsHandler(miner, eventManager);
	}
	
	@NotNull
	public static IPubSubMessageHandler createClaimMomentHandler(@NotNull IMiner miner, @NotNull IEventManager eventManager){
		return new ClaimMomentHandler(miner, eventManager);
	}
	
	@NotNull
	public static IPubSubMessageHandler createClaimDropHandler(@NotNull SyncInventory syncInventory){
		return new ClaimDropHandler(syncInventory);
	}
}
