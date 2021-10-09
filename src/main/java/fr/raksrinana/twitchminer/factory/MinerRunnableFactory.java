package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.runnables.SendMinutesWatched;
import fr.raksrinana.twitchminer.miner.runnables.UpdateChannelPointsContext;
import fr.raksrinana.twitchminer.miner.runnables.UpdateStreamInfo;
import fr.raksrinana.twitchminer.miner.runnables.WebSocketPing;
import org.jetbrains.annotations.NotNull;

public class MinerRunnableFactory{
	@NotNull
	public static UpdateChannelPointsContext getUpdateChannelPointsContext(@NotNull IMiner miner){
		return new UpdateChannelPointsContext(miner);
	}
	
	@NotNull
	public static UpdateStreamInfo getUpdateStreamInfo(@NotNull IMiner miner){
		return new UpdateStreamInfo(miner);
	}
	
	@NotNull
	public static SendMinutesWatched getSendMinutesWatched(@NotNull IMiner miner){
		return new SendMinutesWatched(miner);
	}
	
	@NotNull
	public static WebSocketPing getWebSocketPing(@NotNull IMiner miner){
		return new WebSocketPing(miner);
	}
}
