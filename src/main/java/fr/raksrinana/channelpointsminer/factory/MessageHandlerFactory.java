package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.handler.*;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MessageHandlerFactory{
	public static MessageHandler createLogger(IMiner miner){
		return new EventLoggerHandler(miner);
	}
	
	public static MessageHandler createClaimAvailableHandler(IMiner miner){
		return new ClaimAvailableHandler(miner);
	}
	
	public static MessageHandler createStreamStartEndHandler(IMiner miner){
		return new StreamStartEndHandler(miner);
	}
	
	public static MessageHandler createFollowRaidHandler(IMiner miner){
		return new FollowRaidHandler(miner);
	}
	
	public static MessageHandler createPredictionsHandler(IMiner miner){
		return new PredictionsHandler(miner);
	}
}
