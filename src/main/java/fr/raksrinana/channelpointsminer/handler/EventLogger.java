package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.*;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
@RequiredArgsConstructor
public class EventLogger extends HandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onClaimAvailable(@NotNull Topic topic, @NotNull ClaimAvailable message){
		var streamer = miner.getStreamerById(message.getData().getClaim().getChannelId());
		try(var ignored = LogContext.with(streamer.orElse(null))){
			log.info("Claim available");
		}
	}
	
	@Override
	public void onPointsEarned(@NotNull Topic topic, @NotNull PointsEarned message){
		var streamer = miner.getStreamerById(message.getData().getChannelId());
		try(var ignored = LogContext.with(streamer.orElse(null))){
			var pointGain = message.getData().getPointGain();
			log.info("Points earned +{} ({})", pointGain.getTotalPoints(), pointGain.getReasonCode());
		}
	}
	
	@Override
	public void onPointsSpent(@NotNull Topic topic, @NotNull PointsSpent message){
		var balance = message.getData().getBalance();
		log.info("Points spent ({})", balance.getBalance());
	}
	
	@Override
	public void onStreamDown(@NotNull Topic topic, @NotNull StreamDown message){
		var streamer = miner.getStreamerById(topic.getTarget());
		try(var ignored = LogContext.with(streamer.orElse(null))){
			log.info("Stream stopped");
		}
	}
	
	@Override
	public void onStreamUp(@NotNull Topic topic, @NotNull StreamUp message){
		var streamer = miner.getStreamerById(topic.getTarget());
		try(var ignored = LogContext.with(streamer.orElse(null))){
			log.info("Stream started");
		}
	}
}
