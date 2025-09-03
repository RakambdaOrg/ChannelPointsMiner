package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.DropClaim;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.DropProgress;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.DropClaimedChannelEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.DropProgressChannelEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class ClaimDropHandler extends PubSubMessageHandlerAdapter{
	@NonNull
	private IMiner miner;
	@NonNull
	private IEventManager eventManager;
	
	@Override
	public void onDropProgress(@NonNull Topic topic, @NonNull DropProgress message){
		var channelId = message.getData().getChannelId();
		var streamer = miner.getStreamerById(channelId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		
		try(var ignored = LogContext.with(miner).withStreamer(streamer)){
			var progress = 100 * message.getData().getCurrentProgressMin() / message.getData().getRequiredProgressMin();
			var event = new DropProgressChannelEvent(channelId, username, streamer, TimeFactory.now(), progress);
			eventManager.onEvent(event);
		}
	}
	
	@Override
	public void onDropClaim(@NonNull Topic topic, @NonNull DropClaim message){
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
