package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.PointsEarned;
import fr.raksrinana.channelpointsminer.api.ws.data.message.PointsSpent;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.log.event.PointsEarnedLogEvent;
import fr.raksrinana.channelpointsminer.log.event.PointsSpentLogEvent;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PointsHandler extends HandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onPointsEarned(@NotNull Topic topic, @NotNull PointsEarned message){
		miner.onLogEvent(new PointsEarnedLogEvent(miner, miner.getStreamerById(message.getData().getChannelId()).orElse(null), message.getData()));
	}
	
	@Override
	public void onPointsSpent(@NotNull Topic topic, @NotNull PointsSpent message){
		miner.onLogEvent(new PointsSpentLogEvent(miner, miner.getStreamerById(message.getData().getBalance().getChannelId()).orElse(null), message.getData()));
	}
}
