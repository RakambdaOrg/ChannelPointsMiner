package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.PointsEarned;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.PointsSpent;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsEarnedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsSpentEvent;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@RequiredArgsConstructor
public class PointsHandler extends PubSubMessageHandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onPointsEarned(@NotNull Topic topic, @NotNull PointsEarned message){
		var streamerId = message.getData().getChannelId();
		var streamer = miner.getStreamerById(streamerId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		miner.onEvent(new PointsEarnedEvent(miner, streamerId, username, streamer, message.getData()));
	}
	
	@Override
	public void onPointsSpent(@NotNull Topic topic, @NotNull PointsSpent message){
		var streamerId = message.getData().getBalance().getChannelId();
		var streamer = miner.getStreamerById(streamerId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		miner.onEvent(new PointsSpentEvent(miner, streamerId, username, streamer, message.getData()));
	}
}
