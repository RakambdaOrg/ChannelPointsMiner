package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.ITwitchHermesWebSocketResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ITwitchHermesWebSocketListener {
	void onWebSocketMessage(@NonNull ITwitchHermesWebSocketResponse message);
	
	void onWebSocketClosed(@NonNull TwitchHermesWebSocketClient client, int code, @Nullable String reason, boolean remote);
}
