package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.runnable.HermesWebSocketPing;
import fr.rakambda.channelpointsminer.miner.runnable.SendM3u8MinutesWatched;
import fr.rakambda.channelpointsminer.miner.runnable.SendSpadeMinutesWatched;
import fr.rakambda.channelpointsminer.miner.runnable.StreamerConfigurationReload;
import fr.rakambda.channelpointsminer.miner.runnable.SyncInventory;
import fr.rakambda.channelpointsminer.miner.runnable.UpdateStreamInfo;
import fr.rakambda.channelpointsminer.miner.runnable.ChatWebSocketPing;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MinerRunnableFactory{
	@NonNull
	public static UpdateStreamInfo createUpdateStreamInfo(@NonNull IMiner miner){
		return new UpdateStreamInfo(miner);
	}
	
	@NonNull
	public static SendSpadeMinutesWatched createSendSpadeMinutesWatched(@NonNull IMiner miner){
		return new SendSpadeMinutesWatched(miner);
	}
	@NonNull
	public static SendM3u8MinutesWatched createSendM3u8MinutesWatched(@NonNull IMiner miner){
		return new SendM3u8MinutesWatched(miner);
	}
	
	@NonNull
	public static ChatWebSocketPing createChatWebSocketPing(@NonNull IMiner miner){
		return new ChatWebSocketPing(miner);
	}
	
	@NonNull
	public static HermesWebSocketPing createHermesWebSocketPing(@NonNull IMiner miner){
		return new HermesWebSocketPing(miner);
	}
	
	@NonNull
	public static SyncInventory createSyncInventory(@NonNull IMiner miner, @NonNull IEventManager eventManager){
		return new SyncInventory(miner, eventManager);
	}
	
	@NonNull
	public static StreamerConfigurationReload createStreamerConfigurationReload(@NonNull IMiner miner, @NonNull IEventManager eventManager, @NonNull StreamerSettingsFactory streamerSettingsFactory, boolean loadFollows){
		return new StreamerConfigurationReload(miner, eventManager, streamerSettingsFactory, loadFollows);
	}
}
