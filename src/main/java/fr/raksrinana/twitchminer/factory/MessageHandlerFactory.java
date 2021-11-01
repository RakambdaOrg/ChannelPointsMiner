package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.handler.*;
import fr.raksrinana.twitchminer.miner.IMiner;
import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MessageHandlerFactory{
	public static MessageHandler createLogger(IMiner miner){
		return new EventLogger(miner);
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
}
