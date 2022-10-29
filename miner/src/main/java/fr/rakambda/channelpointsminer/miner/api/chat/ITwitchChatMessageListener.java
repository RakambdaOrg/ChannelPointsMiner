package fr.rakambda.channelpointsminer.miner.api.chat;

import org.jetbrains.annotations.NotNull;

public interface ITwitchChatMessageListener{
	
	void onChatMessage(@NotNull String streamer, @NotNull String actor, @NotNull String message);
	
	void onChatMessage(@NotNull String streamer, @NotNull String actor, @NotNull String message, @NotNull String badges);
}
