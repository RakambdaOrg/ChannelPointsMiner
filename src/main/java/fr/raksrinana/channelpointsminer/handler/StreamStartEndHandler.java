package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.StreamDown;
import fr.raksrinana.channelpointsminer.api.ws.data.message.StreamUp;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.IMiner;
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
		updateStream(topic);
	}
	
	@Override
	public void onStreamUp(@NotNull Topic topic, @NotNull StreamUp message){
		updateStream(topic);
	}
	
	private void updateStream(@NotNull Topic topic){
		try(var context = LogContext.with(miner)){
			var streamerOptional = miner.getStreamerById(topic.getTarget());
			if(streamerOptional.isEmpty()){
				log.warn("Couldn't find associated streamer with target {}", topic.getTarget());
				return;
			}
			
			var streamer = streamerOptional.get();
			context.withStreamer(streamer);
			//Wait that the API updates
			miner.schedule(() -> miner.updateStreamerInfos(streamer), 15, SECONDS);
		}
	}
}
