package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PointsEarned;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PointsSpent;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsEarnedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsSpentEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import java.util.Objects;

@RequiredArgsConstructor
public class PointsHandler extends PubSubMessageHandlerAdapter{
	@NonNull
	private final IMiner miner;
	@NonNull
	private final IEventManager eventManager;
	
	@Override
	public void onPointsEarned(@NonNull Topic topic, @NonNull PointsEarned message){
		var streamerId = message.getData().getChannelId();
		var streamer = miner.getStreamerById(streamerId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		eventManager.onEvent(new PointsEarnedEvent(streamerId, username, streamer, message.getData()));
	}
	
	@Override
	public void onPointsSpent(@NonNull Topic topic, @NonNull PointsSpent message){
		var streamerId = message.getData().getBalance().getChannelId();
		var streamer = miner.getStreamerById(streamerId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		eventManager.onEvent(new PointsSpentEvent(streamerId, username, streamer, message.getData()));
	}
}
