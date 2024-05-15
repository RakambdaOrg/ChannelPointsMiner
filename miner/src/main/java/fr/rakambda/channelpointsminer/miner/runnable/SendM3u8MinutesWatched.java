package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

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
	protected boolean checkStreamer(Streamer streamer){
		return Objects.nonNull(streamer.getM3u8Url());
	}
	
	@Override
	protected boolean send(Streamer streamer){
		return miner.getTwitchApi().openM3u8LastChunk(streamer.getM3u8Url());
	}
}
