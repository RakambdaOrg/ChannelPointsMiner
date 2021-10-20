package fr.raksrinana.twitchminer.miner.handler;

import fr.raksrinana.twitchminer.api.ws.data.message.*;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
@RequiredArgsConstructor
public class EventLogger extends HandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onClaimAvailable(@NotNull Topic topic, @NotNull ClaimAvailable message){
		var channelId = message.getData().getClaim().getChannelId();
		var name = miner.getStreamerById(channelId)
				.map(Streamer::getUsername)
				.orElse(channelId);
		log.info("==> Claim available for {}", name);
	}
	
	@Override
	public void onPointsEarned(@NotNull Topic topic, @NotNull PointsEarned message){
		var pointGain = message.getData().getPointGain();
		log.info("==> Points earned +{} ({})", pointGain.getTotalPoints(), pointGain.getReasonCode());
	}
	
	@Override
	public void onPointsSpent(@NotNull Topic topic, @NotNull PointsSpent message){
		var balance = message.getData().getBalance();
		log.info("==> Points spent ({})", balance.getBalance());
	}
	
	@Override
	public void onStreamDown(@NotNull Topic topic, @NotNull StreamDown message){
		var name = miner.getStreamerById(topic.getTarget())
				.map(Streamer::getUsername)
				.orElse(topic.getTarget());
		log.info("==> Stream stopped {}", name);
	}
	
	@Override
	public void onStreamUp(@NotNull Topic topic, @NotNull StreamUp message){
		var name = miner.getStreamerById(topic.getTarget())
				.map(Streamer::getUsername)
				.orElse(topic.getTarget());
		log.info("==> Stream started {}", name);
	}
}
