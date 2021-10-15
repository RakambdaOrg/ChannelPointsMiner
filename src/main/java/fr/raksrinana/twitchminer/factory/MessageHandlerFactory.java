package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.handler.ClaimAvailableHandler;
import fr.raksrinana.twitchminer.miner.handler.MessageHandler;

public class MessageHandlerFactory{
	public static MessageHandler createClaimAvailableHandler(IMiner miner){
		return new ClaimAvailableHandler(miner);
	}
}
