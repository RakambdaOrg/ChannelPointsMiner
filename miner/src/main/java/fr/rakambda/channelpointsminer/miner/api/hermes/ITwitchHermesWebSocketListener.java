package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.ITwitchHermesWebSocketResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ITwitchHermesWebSocketListener {
	void onWebSocketMessage(@NotNull ITwitchHermesWebSocketResponse message);
	
	void onWebSocketClosed(@NotNull TwitchHermesWebSocketClient client, int code, @Nullable String reason, boolean remote);
}
