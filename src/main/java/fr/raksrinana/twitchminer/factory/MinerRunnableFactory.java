package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.runnables.SendMinutesWatched;
import fr.raksrinana.twitchminer.miner.runnables.UpdateStreamInfo;
import fr.raksrinana.twitchminer.miner.runnables.WebSocketPing;
import org.jetbrains.annotations.NotNull;

public class MinerRunnableFactory{
	@NotNull
	public static UpdateStreamInfo createUpdateStreamInfo(@NotNull IMiner miner){
		return new UpdateStreamInfo(miner);
	}
	
	@NotNull
	public static SendMinutesWatched createSendMinutesWatched(@NotNull IMiner miner){
		return new SendMinutesWatched(miner);
	}
	
	@NotNull
	public static WebSocketPing createWebSocketPing(@NotNull IMiner miner){
		return new WebSocketPing(miner);
	}
}
