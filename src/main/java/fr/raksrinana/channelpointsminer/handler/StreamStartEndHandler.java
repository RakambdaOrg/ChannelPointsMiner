package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.StreamDown;
import fr.raksrinana.channelpointsminer.api.ws.data.message.StreamUp;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.log.LogContext;
import fr.raksrinana.channelpointsminer.log.event.StreamDownLogEvent;
import fr.raksrinana.channelpointsminer.log.event.StreamUpLogEvent;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
@Log4j2
public class StreamStartEndHandler extends HandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onStreamDown(@NotNull Topic topic, @NotNull StreamDown message){
		var streamer = miner.getStreamerById(topic.getTarget()).orElse(null);
		updateStream(topic, streamer);
		miner.onLogEvent(new StreamDownLogEvent(miner, streamer));
	}
	
	@Override
	public void onStreamUp(@NotNull Topic topic, @NotNull StreamUp message){
		var streamer = miner.getStreamerById(topic.getTarget()).orElse(null);
		updateStream(topic, streamer);
		miner.onLogEvent(new StreamUpLogEvent(miner, streamer));
	}
	
	private void updateStream(@NotNull Topic topic, @Nullable Streamer streamer){
		try(var ignored = LogContext.with(miner).withStreamer(streamer)){
			if(Objects.isNull(streamer)){
				log.warn("Couldn't find associated streamer with target {}", topic.getTarget());
				return;
			}
			
			//Wait that the API updates
			miner.schedule(() -> miner.updateStreamerInfos(streamer), 15, SECONDS);
		}
	}
}
