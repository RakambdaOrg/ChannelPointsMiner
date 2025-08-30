package fr.rakambda.channelpointsminer.miner.api.pubsub;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.response.ITwitchWebSocketResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ITwitchPubSubWebSocketListener{
	void onWebSocketMessage(@NotNull ITwitchWebSocketResponse message);
	
	void onWebSocketClosed(@NotNull TwitchPubSubWebSocketClient client, int code, @Nullable String reason, boolean remote);
}
