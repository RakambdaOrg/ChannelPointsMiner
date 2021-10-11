package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.api.ws.data.message.ClaimAvailable;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.handler.ClaimAvailableHandler;
import fr.raksrinana.twitchminer.miner.handler.MessageHandler;

public class MessageHandlerFactory{
	public static MessageHandler<ClaimAvailable> createClaimAvailableHandler(IMiner miner){
		return new ClaimAvailableHandler(miner);
	}
}
