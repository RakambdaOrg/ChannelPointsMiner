package fr.raksrinana.twitchminer.miner.handler;

import fr.raksrinana.twitchminer.api.ws.data.message.ClaimAvailable;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ClaimAvailableHandler implements MessageHandler<ClaimAvailable>{
	private final IMiner miner;
	
	@Override
	public void handle(@NotNull Topic topic, @NotNull ClaimAvailable message){
		miner.getGqlApi().claimCommunityPoints(message.getData().getClaim().getChannelId(), message.getData().getClaim().getId());
	}
}
