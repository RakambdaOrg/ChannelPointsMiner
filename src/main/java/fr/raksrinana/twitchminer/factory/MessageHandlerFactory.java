package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.miner.EventLogger;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.handler.ClaimAvailableHandler;
import fr.raksrinana.twitchminer.miner.handler.MessageHandler;
import fr.raksrinana.twitchminer.miner.handler.StreamStartEndHandler;

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
}
