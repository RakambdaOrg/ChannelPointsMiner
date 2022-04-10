package fr.raksrinana.channelpointsminer.miner.api.ws;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.response.ITwitchWebSocketResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ITwitchWebSocketListener{
	void onWebSocketMessage(@NotNull ITwitchWebSocketResponse message);
	
	void onWebSocketClosed(@NotNull TwitchWebSocketClient client, int code, @Nullable String reason, boolean remote);
}
