package fr.raksrinana.channelpointsminer.miner.api.chat;

import org.jetbrains.annotations.NotNull;

public interface ITwitchChatClient extends AutoCloseable{
	void join(@NotNull String channel);
	
	void leave(@NotNull String channel);
	
	void ping();
	
	@Override
	void close();
}
