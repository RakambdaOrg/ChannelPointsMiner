package fr.rakambda.channelpointsminer.miner.api.chat;

import org.jspecify.annotations.NonNull;

public interface ITwitchChatClient extends AutoCloseable{
	void join(@NonNull String channel);
	
	void joinPending();
	
	void leave(@NonNull String channel);
	
	void ping();
	
	@Override
	void close();
	
	void addChatMessageListener(@NonNull ITwitchChatMessageListener listener);
}
