package fr.raksrinana.channelpointsminer.miner.handler;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.RaidUpdateV2;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Log4j2
public class FollowRaidHandler extends TwitchWsEventHandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onRaidUpdateV2(@NotNull Topic topic, @NotNull RaidUpdateV2 message){
		try(var context = LogContext.with(miner)){
			var streamerOptional = miner.getStreamerById(topic.getTarget());
			if(streamerOptional.isEmpty()){
				log.warn("Couldn't find associated streamer with target {}", topic.getTarget());
				return;
			}
			
			var streamer = streamerOptional.get();
			context.withStreamer(streamer);
			miner.getGqlApi().joinRaid(message.getRaid().getId());
		}
	}
}
