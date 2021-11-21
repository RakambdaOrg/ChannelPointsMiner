package fr.raksrinana.channelpointsminer.miner;

import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.channelpointsminer.log.ILogEventListener;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface IMiner extends ILogEventListener{
	@NotNull
	String getUsername();
	
	@NotNull
	Collection<Streamer> getStreamers();
	
	@NotNull
	Optional<Streamer> getStreamerById(@NotNull String id);
	
	@NotNull
	TwitchWebSocketPool getWebSocketPool();
	
	@Nullable
	TwitchLogin getTwitchLogin();
	
	@Nullable
	GQLApi getGqlApi();
	
	@Nullable
	TwitchApi getTwitchApi();
	
	@NotNull
	MinerData getMinerData();
	
	/**
	 * Add a streamer to the list being mined.
	 *
	 * @param streamer Streamer to add.
	 */
	void addStreamer(@NotNull Streamer streamer);
	
	void updateStreamer(@NotNull Streamer streamer);
	
	boolean removeStreamer(@NotNull Streamer streamer);
	
	void updateStreamerInfos(@NotNull Streamer streamer);
	
	boolean containsStreamer(@NotNull Streamer streamer);
	
	@NotNull
	ScheduledFuture<?> schedule(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit);
}
