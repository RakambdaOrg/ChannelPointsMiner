package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.api.ws.TwitchMessageListener;
import fr.raksrinana.twitchminer.api.ws.data.message.*;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
@RequiredArgsConstructor
public class EventLogger implements TwitchMessageListener{
	private final IMiner miner;
	
	@Override
	public void onTwitchMessage(@NotNull Topic topic, @NotNull Message message){
		if(message instanceof PointsEarned pointsEarned){
			var pointGain = pointsEarned.getData().getPointGain();
			log.info("==> Points earned +{} ({})", pointGain.getTotalPoints(), pointGain.getReasonCode());
		}
		else if(message instanceof StreamUp streamUp){
			log.info("==> Stream started {}");
		}
		else if(message instanceof StreamDown streamDown){
			log.info("==> Stream stopped {}");
		}
		else if(message instanceof ClaimAvailable claimAvailable){
			log.info("==> Claim available {}", claimAvailable.getData().getClaim().getChannelId());
		}
	}
}
