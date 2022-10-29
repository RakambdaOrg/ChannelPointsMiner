package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.ClaimAvailable;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.ClaimAvailableEvent;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@RequiredArgsConstructor
public class ClaimAvailableHandler extends PubSubMessageHandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onClaimAvailable(@NotNull Topic topic, @NotNull ClaimAvailable message){
		var channelId = message.getData().getClaim().getChannelId();
		var streamer = miner.getStreamerById(channelId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		try(var ignored = LogContext.with(miner).withStreamer(streamer)){
			miner.onEvent(new ClaimAvailableEvent(miner, channelId, username, streamer, message.getData().getTimestamp().toInstant()));
			miner.getGqlApi().claimCommunityPoints(channelId, message.getData().getClaim().getId());
		}
	}
}
