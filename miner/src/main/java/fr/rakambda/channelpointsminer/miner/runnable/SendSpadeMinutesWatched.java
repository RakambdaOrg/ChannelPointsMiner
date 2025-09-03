package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Game;
import fr.rakambda.channelpointsminer.miner.api.twitch.data.MinuteWatchedEvent;
import fr.rakambda.channelpointsminer.miner.api.twitch.data.MinuteWatchedProperties;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.priority.IStreamerPriority;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import java.util.Objects;
import java.util.function.Predicate;

@Log4j2
public class SendSpadeMinutesWatched extends SendMinutesWatched{
	public SendSpadeMinutesWatched(@NonNull IMiner miner){
		super(miner);
	}
	
	@Override
	protected String getType(){
		return "Spade";
	}
	
	@Override
	protected boolean checkStreamer(@NonNull Streamer streamer){
		return Objects.nonNull(streamer.getSpadeUrl());
	}
	
	@Override
	protected boolean send(@NonNull Streamer streamer){
		var streamId = streamer.getStreamId();
		if(streamId.isEmpty()){
			return false;
		}
		
		var request = MinuteWatchedEvent.builder()
				.properties(MinuteWatchedProperties.builder()
						.channelId(streamer.getId())
						.channel(streamer.getUsername())
						.broadcastId(streamId.get())
						.player("site")
						.userId(miner.getTwitchLogin().getUserIdAsInt(miner.getGqlApi()))
						.gameId(streamer.getGame().map(Game::getId).orElse(null))
						.game(streamer.getGame().map(Game::getName).orElse(null))
						.live(true)
						.build())
				.build();
		
		return miner.getTwitchApi().sendPlayerEvents(streamer.getSpadeUrl(), request);
	}
	
	@Override
	protected boolean shouldUpdateWatchedMinutes(){
		return true;
	}
	
	@Override
	@NonNull
	protected Predicate<IStreamerPriority> getPriorityFilter(){
		return Predicate.not(IStreamerPriority::isDropsRelated);
	}
	
	@Override
	protected int getWatchLimit(){
		return 2;
	}
}
