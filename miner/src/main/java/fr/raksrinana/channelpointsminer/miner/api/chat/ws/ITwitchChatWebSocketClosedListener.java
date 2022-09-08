package fr.raksrinana.channelpointsminer.miner.api.chat.ws;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ITwitchChatWebSocketClosedListener{
	void onWebSocketClosed(@NotNull TwitchChatWebSocketClient client, int code, @Nullable String reason, boolean remote);
}
