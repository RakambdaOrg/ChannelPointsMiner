package fr.raksrinana.twitchminer.api.ws;

import fr.raksrinana.twitchminer.api.ws.data.response.TwitchWebSocketResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TwitchWebSocketListener{
	void onWebSocketMessage(@NotNull TwitchWebSocketResponse message);
	
	default void onWebSocketClosed(@NotNull TwitchWebSocketClient client, int code, @Nullable String reason, boolean remote){};
}
