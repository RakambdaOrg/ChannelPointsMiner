package fr.raksrinana.channelpointsminer.api.ws;

import fr.raksrinana.channelpointsminer.api.ws.data.response.TwitchWebSocketResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TwitchWebSocketListener{
	void onWebSocketMessage(@NotNull TwitchWebSocketResponse message);
	
	void onWebSocketClosed(@NotNull TwitchWebSocketClient client, int code, @Nullable String reason, boolean remote);
}
