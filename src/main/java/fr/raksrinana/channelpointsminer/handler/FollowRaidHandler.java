package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.RaidUpdateV2;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Log4j2
public class FollowRaidHandler extends HandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onRaidUpdateV2(@NotNull Topic topic, @NotNull RaidUpdateV2 message){
		var streamerOptional = miner.getStreamerById(topic.getTarget());
		if(streamerOptional.isEmpty()){
			log.warn("Couldn't find associated streamer with target {}", topic.getTarget());
			return;
		}
		
		var streamer = streamerOptional.get();
		try(var ignored = LogContext.with(streamer)){
			if(streamer.followRaids()){
				miner.getGqlApi().joinRaid(message.getRaid().getId());
			}
		}
	}
}
