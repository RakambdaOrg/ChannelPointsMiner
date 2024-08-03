package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.priority.IStreamerPriority;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.function.Predicate;

@Log4j2
public class SendM3u8MinutesWatched extends SendMinutesWatched{
	public SendM3u8MinutesWatched(@NotNull IMiner miner){
		super(miner);
	}
	
	@Override
	protected String getType(){
		return "M3U8";
	}
	
	@Override
	protected boolean checkStreamer(@NotNull Streamer streamer){
		return Objects.nonNull(streamer.getM3u8Url());
	}
	
	@Override
	protected boolean send(@NotNull Streamer streamer){
		var result = miner.getTwitchApi().openM3u8LastChunk(streamer.getM3u8Url());
		if(!result){
			log.warn("Got an error from m3u8 for streamer, disabling it until next stream data refresh");
			streamer.setM3u8Url(null);
		}
		return result;
	}
	
	@Override
	protected boolean shouldUpdateWatchedMinutes(){
		return false;
	}
	
	@Override
	@NotNull
	protected Predicate<IStreamerPriority> getPriorityFilter(){
		return IStreamerPriority::isDropsRelated;
	}
	
	@Override
	protected int getWatchLimit(){
		return 1;
	}
}
