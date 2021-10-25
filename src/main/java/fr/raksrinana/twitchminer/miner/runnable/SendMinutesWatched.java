package fr.raksrinana.twitchminer.miner.runnable;

import fr.raksrinana.twitchminer.api.gql.data.types.Game;
import fr.raksrinana.twitchminer.api.twitch.data.MinuteWatchedEvent;
import fr.raksrinana.twitchminer.api.twitch.data.MinuteWatchedProperties;
import fr.raksrinana.twitchminer.factory.TimeFactory;
import fr.raksrinana.twitchminer.log.LogContext;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import fr.raksrinana.twitchminer.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
public class SendMinutesWatched implements Runnable{
	private static final String SITE_PLAYER = "site";
	
	@NotNull
	private final IMiner miner;
	private final Map<String, Instant> lastSend = new HashMap<>();
	
	@Override
	public void run(){
		log.debug("Sending all minutes watched");
		try{
			var toSendMinutesWatched = miner.getStreamers().stream()
					.filter(Streamer::isStreaming)
					.filter(streamer -> Objects.nonNull(streamer.getSpadeUrl()))
					.map(streamer -> Map.entry(streamer, streamer.getScore()))
					.sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
					.limit(2)
					.map(Map.Entry::getKey)
					.toList();
			
			for(var streamer : toSendMinutesWatched){
				if(send(streamer)){
					updateWatchedMinutes(streamer);
				}
				CommonUtils.randomSleep(100, 50);
			}
			
			removeLastSend(toSendMinutesWatched);
			
			log.debug("Done all sending minutes watched");
		}
		catch(Exception e){
			log.error("Failed to send all minutes watched", e);
		}
	}
	
	private boolean send(Streamer streamer){
		try(var ignored = LogContext.with(streamer)){
			log.debug("Sending minutes watched");
			var streamId = streamer.getStreamId();
			if(streamId.isEmpty()){
				return false;
			}
			
			var request = MinuteWatchedEvent.builder()
					.properties(MinuteWatchedProperties.builder()
							.channelId(streamer.getId())
							.broadcastId(streamId.get())
							.player(SITE_PLAYER)
							.userId(miner.getTwitchLogin().getUserIdAsInt())
							.game(streamer.getGame().map(Game::getName).orElse(null))
							.build())
					.build();
			
			return miner.getTwitchApi().sendPlayerEvents(streamer.getSpadeUrl(), request);
		}
	}
	
	private void updateWatchedMinutes(@NotNull Streamer streamer){
		var now = TimeFactory.now();
		var previousUpdate = lastSend.get(streamer.getId());
		if(Objects.nonNull(previousUpdate)){
			var duration = Duration.between(previousUpdate, now);
			streamer.addMinutesWatched(duration);
		}
		lastSend.put(streamer.getId(), now);
	}
	
	private void removeLastSend(@NotNull List<Streamer> currentStreamers){
		var currentIds = currentStreamers.stream().map(Streamer::getId).toList();
		var keysToRemove = lastSend.keySet().stream().filter(id -> !currentIds.contains(id)).toList();
		keysToRemove.forEach(lastSend::remove);
	}
}
