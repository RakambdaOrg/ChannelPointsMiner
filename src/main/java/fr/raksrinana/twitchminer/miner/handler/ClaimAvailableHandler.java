package fr.raksrinana.twitchminer.miner.handler;

import fr.raksrinana.twitchminer.api.ws.data.message.ClaimAvailable;
import fr.raksrinana.twitchminer.miner.IMiner;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClaimAvailableHandler implements MessageHandler<ClaimAvailable>{
	private final IMiner miner;
	
	@Override
	public void handle(ClaimAvailable message){
		miner.getGqlApi().claimCommunityPoints(message.getData().getClaim().getChannelId(), message.getData().getClaim().getId());
	}
}
