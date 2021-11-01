package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.runnable.SendMinutesWatched;
import fr.raksrinana.twitchminer.runnable.SyncInventory;
import fr.raksrinana.twitchminer.runnable.UpdateStreamInfo;
import fr.raksrinana.twitchminer.runnable.WebSocketPing;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
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
	
	@NotNull
	public static SyncInventory createSyncInventory(@NotNull IMiner miner){
		return new SyncInventory(miner);
	}
}
