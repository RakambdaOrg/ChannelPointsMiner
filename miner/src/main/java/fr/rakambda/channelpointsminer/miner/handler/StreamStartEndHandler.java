package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.BroadcastSettingsUpdate;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.StreamDown;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.StreamUp;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamDownEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamUpEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
@Log4j2
public class StreamStartEndHandler extends PubSubMessageHandlerAdapter{
	@NotNull
	private final IMiner miner;
	@NotNull
	private final IEventManager eventManager;
	
	@Override
	public void onStreamUp(@NotNull Topic topic, @NotNull StreamUp message){
		var streamerId = topic.getTarget();
		var streamer = miner.getStreamerById(streamerId);
		streamUp(streamerId, streamer.orElse(null), streamer.map(Streamer::getUsername).orElse(null), message.getServerTime(), true);
	}
	
	@Override
	public void onStreamDown(@NotNull Topic topic, @NotNull StreamDown message){
		var streamerId = topic.getTarget();
		var streamer = miner.getStreamerById(streamerId);
		streamDown(streamerId, streamer.orElse(null), streamer.map(Streamer::getUsername).orElse(null), message.getServerTime(), true);
	}
	
	@Override
	public void onBroadcastSettingsUpdate(@NotNull Topic topic, @NotNull BroadcastSettingsUpdate message){
		var streamerId = message.getChannelId();
		var streamer = miner.getStreamerById(streamerId);
		
		if(streamer.map(Streamer::isStreaming).orElse(true)){
			streamDown(streamerId, streamer.orElse(null), streamer.map(Streamer::getUsername).orElse(null), TimeFactory.now(), false);
		}
		else{
			streamUp(streamerId, streamer.orElse(null), streamer.map(Streamer::getUsername).orElse(null), TimeFactory.now(), false);
		}
	}
	
	private void streamUp(@NotNull String streamerId, @Nullable Streamer streamer, @Nullable String username, @NotNull Instant serverTime, boolean fireEvent){
		updateStream(streamerId, streamer);
		Optional.ofNullable(streamer)
				.filter(s -> s.getSettings().isJoinIrc())
				.map(Streamer::getUsername)
				.ifPresent(miner.getChatClient()::join);
		if(fireEvent){
			eventManager.onEvent(new StreamUpEvent(streamerId, username, streamer, serverTime));
		}
	}
	
	private void streamDown(@NotNull String streamerId, @Nullable Streamer streamer, @Nullable String username, @NotNull Instant serverTime, boolean fireEvent){
		updateStream(streamerId, streamer);
		Optional.ofNullable(streamer)
				.map(Streamer::getUsername)
				.ifPresent(miner.getChatClient()::leave);
		if(fireEvent){
			eventManager.onEvent(new StreamDownEvent(streamerId, username, streamer, serverTime));
		}
	}
	
	private void updateStream(@Nullable String streamerId, @Nullable Streamer streamer){
		try(var ignored = LogContext.with(miner).withStreamer(streamer)){
			if(Objects.isNull(streamer)){
				log.warn("Couldn't find associated streamer with id {}, not updating its info", streamerId);
				return;
			}
			
			//Wait that the API updates
			miner.schedule(() -> miner.updateStreamerInfos(streamer), 15, SECONDS);
		}
	}
}
