package fr.rakambda.channelpointsminer.miner.api.chat;

import org.jspecify.annotations.NonNull;

public interface ITwitchChatMessageListener{
	
	void onChatMessage(@NonNull String streamer, @NonNull String actor, @NonNull String message);
	
	void onChatMessage(@NonNull String streamer, @NonNull String actor, @NonNull String message, @NonNull String badges);
}
