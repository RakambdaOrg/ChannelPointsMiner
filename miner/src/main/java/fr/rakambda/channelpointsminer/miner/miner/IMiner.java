package fr.rakambda.channelpointsminer.miner.miner;

import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.rakambda.channelpointsminer.miner.api.hermes.TwitchHermesWebSocketPool;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.rakambda.channelpointsminer.miner.api.pubsub.TwitchPubSubWebSocketPool;
import fr.rakambda.channelpointsminer.miner.database.IDatabase;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface IMiner{
	@NonNull
	Optional<Streamer> getStreamerById(@NonNull String id);
	
	/**
	 * Add a streamer to the list being mined.
	 *
	 * @param streamer Streamer to add.
	 */
	void addStreamer(@NonNull Streamer streamer);
	
	void updateStreamer(@NonNull Streamer streamer);
	
	boolean removeStreamer(@NonNull Streamer streamer);
	
	void updateStreamerInfos(@NonNull Streamer streamer);
	
	void syncInventory();
	
	boolean containsStreamer(@NonNull Streamer streamer);
	
	@NonNull
	ScheduledFuture<?> schedule(@NonNull Runnable runnable, long delay, @NonNull TimeUnit unit);
	
	@Nullable
	GQLApi getGqlApi();
	
	@NonNull
	ITwitchChatClient getChatClient();
	
	@NonNull
	MinerData getMinerData();
	
	@NonNull
	Collection<Streamer> getStreamers();
	
	@Nullable
	TwitchApi getTwitchApi();
	
	@Nullable
	TwitchLogin getTwitchLogin();
	
	@NonNull
	String getUsername();
	
	@NonNull
	TwitchHermesWebSocketPool getHermesWebSocketPool();
	
	@NonNull
	IDatabase getDatabase();
}
