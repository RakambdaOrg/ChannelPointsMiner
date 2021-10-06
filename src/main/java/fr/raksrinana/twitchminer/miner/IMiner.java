package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;

public interface IMiner{
	@NotNull
	Collection<Streamer> getStreamers();
	
	@NotNull
	TwitchWebSocketPool getWebSocketPool();
}
