package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.DropClaim;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.DropProgress;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.DropClaimedChannelEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.DropProgressChannelEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class ClaimDropHandler extends PubSubMessageHandlerAdapter{
	@NotNull
	private IMiner miner;
	@NotNull
	private IEventManager eventManager;
	
	@Override
	public void onDropProgress(@NotNull Topic topic, @NotNull DropProgress message){
		var channelId = message.getData().getChannelId();
		var streamer = miner.getStreamerById(channelId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		
		try(var ignored = LogContext.with(miner).withStreamer(streamer)){
			var progress = message.getData().getCurrentProgressMin() / ((float) message.getData().getRequiredProgressMin());
			var event = new DropProgressChannelEvent(channelId, username, streamer, TimeFactory.now(), progress);
			eventManager.onEvent(event);
		}
	}
	
	@Override
	public void onDropClaim(@NotNull Topic topic, @NotNull DropClaim message){
		var channelId = message.getData().getChannelId();
		var streamer = miner.getStreamerById(channelId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		
		try(var ignored = LogContext.with(miner).withStreamer(streamer)){
			miner.getGqlApi().dropsPageClaimDropRewards(message.getData().getDropInstanceId())
					.filter(r -> {
						if(!r.isError()){
							return true;
						}
						log.error("Failed to claim drop due to `{}` | {}", r.getError(), r.getErrors());
						return false;
					})
					.map(r -> new DropClaimedChannelEvent(channelId, username, streamer, TimeFactory.now()))
					.ifPresent(eventManager::onEvent);
		}
	}
}
