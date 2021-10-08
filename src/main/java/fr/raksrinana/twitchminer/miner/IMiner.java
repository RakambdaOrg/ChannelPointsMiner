package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;

public interface IMiner{
	@NotNull
	Collection<Streamer> getStreamers();
	
	@NotNull
	TwitchWebSocketPool getWebSocketPool();
	
	@Nullable
	TwitchLogin getTwitchLogin();
	
	@Nullable
	GQLApi getGqlApi();
	
	/**
	 * Add a streamer to the list being mined.
	 *
	 * @param streamer Streamer to add.
	 */
	void addStreamer(@NotNull Streamer streamer);
	
	/**
	 * Check if a streamer is being mined.
	 *
	 * @param username Streamer's username.
	 *
	 * @return True if being mined, false otherwise.
	 */
	boolean hasStreamerWithUsername(@NotNull String username);
}
