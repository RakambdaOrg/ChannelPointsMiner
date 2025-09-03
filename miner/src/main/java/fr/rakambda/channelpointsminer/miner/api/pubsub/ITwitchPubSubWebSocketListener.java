package fr.rakambda.channelpointsminer.miner.api.pubsub;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.response.ITwitchWebSocketResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ITwitchPubSubWebSocketListener{
	void onWebSocketMessage(@NonNull ITwitchWebSocketResponse message);
	
	void onWebSocketClosed(@NonNull TwitchPubSubWebSocketClient client, int code, @Nullable String reason, boolean remote);
}
