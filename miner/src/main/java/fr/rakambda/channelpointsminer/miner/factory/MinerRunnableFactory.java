package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.runnable.SendMinutesWatched;
import fr.rakambda.channelpointsminer.miner.runnable.StreamerConfigurationReload;
import fr.rakambda.channelpointsminer.miner.runnable.SyncInventory;
import fr.rakambda.channelpointsminer.miner.runnable.UpdateStreamInfo;
import fr.rakambda.channelpointsminer.miner.runnable.WebSocketPing;
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
	public static SyncInventory createSyncInventory(@NotNull IMiner miner, @NotNull IEventManager eventManager){
		return new SyncInventory(miner, eventManager);
	}
	
	@NotNull
	public static StreamerConfigurationReload createStreamerConfigurationReload(@NotNull IMiner miner, @NotNull IEventManager eventManager, @NotNull StreamerSettingsFactory streamerSettingsFactory, boolean loadFollows){
		return new StreamerConfigurationReload(miner, eventManager, streamerSettingsFactory, loadFollows);
	}
}
