package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.api.ws.data.message.ClaimAvailable;
import fr.raksrinana.twitchminer.api.ws.data.message.PointsEarned;
import fr.raksrinana.twitchminer.api.ws.data.message.StreamDown;
import fr.raksrinana.twitchminer.api.ws.data.message.StreamUp;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import fr.raksrinana.twitchminer.miner.handler.HandlerAdapter;
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
