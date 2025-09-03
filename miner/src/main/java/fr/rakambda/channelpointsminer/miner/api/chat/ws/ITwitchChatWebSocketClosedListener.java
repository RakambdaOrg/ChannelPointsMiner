package fr.rakambda.channelpointsminer.miner.api.chat.ws;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ITwitchChatWebSocketClosedListener{
	void onWebSocketClosed(@NonNull TwitchChatWebSocketClient client, int code, @Nullable String reason, boolean remote);
}
