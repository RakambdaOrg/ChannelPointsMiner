package fr.raksrinana.twitchminer.api.ws;

import fr.raksrinana.twitchminer.api.ws.data.response.TwitchWebSocketResponse;
import org.jetbrains.annotations.NotNull;

public interface TwitchWebSocketListener{
	void onMessage(@NotNull TwitchWebSocketResponse message);
}
