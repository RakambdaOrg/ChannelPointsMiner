package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.ClaimAvailable;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ClaimAvailableHandler extends HandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onClaimAvailable(@NotNull Topic topic, @NotNull ClaimAvailable message){
		var streamer = miner.getStreamerById(message.getData().getClaim().getChannelId());
		try(var ignored = LogContext.with(streamer.orElse(null))){
			miner.getGqlApi().claimCommunityPoints(message.getData().getClaim().getChannelId(), message.getData().getClaim().getId());
		}
	}
}
