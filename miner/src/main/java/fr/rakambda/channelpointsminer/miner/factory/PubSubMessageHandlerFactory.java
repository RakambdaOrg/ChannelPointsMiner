package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.handler.ClaimAvailableHandler;
import fr.rakambda.channelpointsminer.miner.handler.ClaimDropHandler;
import fr.rakambda.channelpointsminer.miner.handler.ClaimMomentHandler;
import fr.rakambda.channelpointsminer.miner.handler.FollowRaidHandler;
import fr.rakambda.channelpointsminer.miner.handler.IPubSubMessageHandler;
import fr.rakambda.channelpointsminer.miner.handler.NotificationHandler;
import fr.rakambda.channelpointsminer.miner.handler.PointsHandler;
import fr.rakambda.channelpointsminer.miner.handler.PredictionsHandler;
import fr.rakambda.channelpointsminer.miner.handler.StreamStartEndHandler;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.prediction.bet.BetPlacer;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PubSubMessageHandlerFactory{
	@NonNull
	public static IPubSubMessageHandler createClaimAvailableHandler(@NonNull IMiner miner, @NonNull IEventManager eventManager){
		return new ClaimAvailableHandler(miner, eventManager);
	}
	
	@NonNull
	public static IPubSubMessageHandler createStreamStartEndHandler(@NonNull IMiner miner, @NonNull IEventManager eventManager){
		return new StreamStartEndHandler(miner, eventManager);
	}
	
	@NonNull
	public static IPubSubMessageHandler createFollowRaidHandler(@NonNull IMiner miner){
		return new FollowRaidHandler(miner);
	}
	
	@NonNull
	public static IPubSubMessageHandler createPredictionsHandler(@NonNull IMiner miner, @NonNull BetPlacer betPlacer, @NonNull IEventManager eventManager){
		return new PredictionsHandler(miner, betPlacer, eventManager);
	}
	
	@NonNull
	public static IPubSubMessageHandler createPointsHandler(@NonNull IMiner miner, @NonNull IEventManager eventManager){
		return new PointsHandler(miner, eventManager);
	}
	
	@NonNull
	public static IPubSubMessageHandler createClaimMomentHandler(@NonNull IMiner miner, @NonNull IEventManager eventManager){
		return new ClaimMomentHandler(miner, eventManager);
	}
	
	@NonNull
	public static IPubSubMessageHandler createClaimDropHandler(@NonNull IMiner miner, @NonNull IEventManager eventManager){
		return new ClaimDropHandler(miner, eventManager);
	}
	
	@NonNull
	public static IPubSubMessageHandler createNotificationHandler(@NonNull IMiner miner){
		return new NotificationHandler(miner);
	}
}
