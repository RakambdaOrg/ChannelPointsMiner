package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.StreamDown;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.StreamUp;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamDownEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamUpEvent;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.Optional;
import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
@Log4j2
public class StreamStartEndHandler extends PubSubMessageHandlerAdapter{
	private final IMiner miner;
	
	@Override
	public void onStreamDown(@NotNull Topic topic, @NotNull StreamDown message){
		var streamerId = topic.getTarget();
		var streamer = miner.getStreamerById(streamerId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		updateStream(topic, streamer);
		Optional.ofNullable(streamer)
				.map(Streamer::getUsername)
				.ifPresent(miner.getChatClient()::leave);
		miner.onEvent(new StreamDownEvent(miner, streamerId, username, streamer, message.getServerTime()));
	}
	
	@Override
	public void onStreamUp(@NotNull Topic topic, @NotNull StreamUp message){
		var streamerId = topic.getTarget();
		var streamer = miner.getStreamerById(streamerId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		updateStream(topic, streamer);
		Optional.ofNullable(streamer)
				.filter(s -> s.getSettings().isJoinIrc())
				.map(Streamer::getUsername)
				.ifPresent(miner.getChatClient()::join);
		miner.onEvent(new StreamUpEvent(miner, streamerId, username, streamer, message.getServerTime()));
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
