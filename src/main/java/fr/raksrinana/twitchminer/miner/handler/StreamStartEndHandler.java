package fr.raksrinana.twitchminer.miner.handler;

import fr.raksrinana.twitchminer.api.ws.data.message.StreamDown;
import fr.raksrinana.twitchminer.api.ws.data.message.StreamUp;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.miner.IMiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
@Log4j2
public class StreamStartEndHandler extends HandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onStreamDown(@NotNull Topic topic, @NotNull StreamDown message){
		var streamerOptional = miner.getStreamerById(topic.getTarget());
		if(streamerOptional.isEmpty()){
			log.warn("Couldn't find associated streamer with target {}", topic.getTarget());
			return;
		}
		//Wait that the API updates
		miner.schedule(() -> miner.updateStreamerInfos(streamerOptional.get()), 15, SECONDS);
	}
	
	@Override
	public void onStreamUp(@NotNull Topic topic, @NotNull StreamUp message){
		var streamerOptional = miner.getStreamerById(topic.getTarget());
		if(streamerOptional.isEmpty()){
			log.warn("Couldn't find associated streamer with target {}", topic.getTarget());
			return;
		}
		//Wait that the API updates
		miner.schedule(() -> miner.updateStreamerInfos(streamerOptional.get()), 15, SECONDS);
	}
}
