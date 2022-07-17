package fr.raksrinana.channelpointsminer.miner.miner;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.raksrinana.channelpointsminer.miner.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.miner.api.ws.TwitchPubSubWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.event.IEventListener;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface IMiner extends IEventListener{
	@NotNull
	Optional<Streamer> getStreamerById(@NotNull String id);
	
	/**
	 * Add a streamer to the list being mined.
	 *
	 * @param streamer Streamer to add.
	 */
	void addStreamer(@NotNull Streamer streamer);
	
	void updateStreamer(@NotNull Streamer streamer);
	
	boolean removeStreamer(@NotNull Streamer streamer);
	
	void updateStreamerInfos(@NotNull Streamer streamer);
	
	void syncInventory();
	
	boolean containsStreamer(@NotNull Streamer streamer);
	
	@NotNull
	ScheduledFuture<?> schedule(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit);
	
	@Nullable
	GQLApi getGqlApi();
	
	@NotNull
	ITwitchChatClient getChatClient();
	
	@NotNull
	MinerData getMinerData();
	
	@NotNull
	Collection<Streamer> getStreamers();
	
	@Nullable
	TwitchApi getTwitchApi();
	
	@Nullable
	TwitchLogin getTwitchLogin();
	
	@NotNull
	String getUsername();
	
	@NotNull
	TwitchPubSubWebSocketPool getWebSocketPool();
}
