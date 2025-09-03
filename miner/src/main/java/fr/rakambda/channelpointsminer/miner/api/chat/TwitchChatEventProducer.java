package fr.rakambda.channelpointsminer.miner.api.chat;

import fr.rakambda.channelpointsminer.miner.event.impl.ChatMessageEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public class TwitchChatEventProducer implements ITwitchChatMessageListener{
	@NonNull
	private final IEventManager eventManager;
	
	@Override
	public void onChatMessage(@NonNull String streamer, @NonNull String actor, @NonNull String message){
		onChatMessage(streamer, actor, message, "");
	}
	
	@Override
	public void onChatMessage(@NonNull String streamer, @NonNull String actor, @NonNull String message, @NonNull String badges){
		var event = new ChatMessageEvent(TimeFactory.now(), streamer, actor, message, badges);
		eventManager.onEvent(event);
	}
}
