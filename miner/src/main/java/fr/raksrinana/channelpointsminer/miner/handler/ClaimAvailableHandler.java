package fr.raksrinana.channelpointsminer.miner.handler;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.ClaimAvailable;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.event.impl.ClaimAvailableEvent;
import fr.raksrinana.channelpointsminer.miner.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@RequiredArgsConstructor
public class ClaimAvailableHandler extends HandlerAdapter{
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
