package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
@Log4j2
public class StreamStartEndHandler extends PubSubMessageHandlerAdapter{
	@NonNull
	private final IMiner miner;
	@NonNull
	private final IEventManager eventManager;
	
	@Override
	public void onStreamUp(@NonNull Topic topic, @NonNull StreamUp message){
		var streamerId = topic.getTarget();
		var streamer = miner.getStreamerById(streamerId);
		streamUp(streamerId, streamer.orElse(null), streamer.map(Streamer::getUsername).orElse(null), message.getServerTime(), true);
	}
	
	@Override
	public void onStreamDown(@NonNull Topic topic, @NonNull StreamDown message){
		var streamerId = topic.getTarget();
		var streamer = miner.getStreamerById(streamerId);
		streamDown(streamerId, streamer.orElse(null), streamer.map(Streamer::getUsername).orElse(null), message.getServerTime(), true);
	}
	
	// @Override
	// public void onBroadcastSettingsUpdate(@NonNull Topic topic, @NonNull BroadcastSettingsUpdate message){
	// 	var streamerId = message.getChannelId();
	// 	var streamer = miner.getStreamerById(streamerId);
	//	
	// 	var status = miner.getGqlApi().withIsStreamLive(streamerId);
	// 	var streaming = status.map(GQLResponse::getData).map(d -> Objects.nonNull(d.getUser().getStream()));
	// 	var memoryStreaming = streamer.map(Streamer::isStreaming).orElse(false);
	//	
	// 	// Fire event only if we know the current streaming status, and it is different from what we currently have in memory (i.e. the stream status changed and not just some parameters)
	// 	var fireEvent = streaming.isPresent() && streaming.get() != memoryStreaming;
	//	
	// 	if(streaming.orElseGet(() -> !memoryStreaming)){
	// 		streamUp(streamerId, streamer.orElse(null), streamer.map(Streamer::getUsername).orElse(null), TimeFactory.now(), fireEvent);
	// 	}
	// 	else{
	// 		streamDown(streamerId, streamer.orElse(null), streamer.map(Streamer::getUsername).orElse(null), TimeFactory.now(), fireEvent);
	// 	}
	// }
	
	private void streamUp(@NonNull String streamerId, @Nullable Streamer streamer, @Nullable String username, @NonNull Instant serverTime, boolean fireEvent){
		updateStream(streamerId, streamer);
		if(fireEvent){
			Optional.ofNullable(streamer)
					.filter(s -> s.getSettings().isJoinIrc())
					.map(Streamer::getUsername)
					.ifPresent(miner.getChatClient()::join);
			eventManager.onEvent(new StreamUpEvent(streamerId, username, streamer, serverTime));
		}
	}
	
	private void streamDown(@NonNull String streamerId, @Nullable Streamer streamer, @Nullable String username, @NonNull Instant serverTime, boolean fireEvent){
		updateStream(streamerId, streamer);
		if(fireEvent){
			Optional.ofNullable(streamer)
					.map(Streamer::getUsername)
					.ifPresent(miner.getChatClient()::leave);
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
