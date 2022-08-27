package fr.raksrinana.channelpointsminer.miner.api.chat;

import fr.raksrinana.channelpointsminer.miner.event.impl.ChatMessageEvent;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class TwitchChatEventProducer implements ITwitchChatMessageListener{
	private final IMiner miner;
	
	@Override
	public void onChatMessage(@NotNull String streamer, @NotNull String actor, @NotNull String message){
		onChatMessage(streamer, actor, message, "");
	}
	
	@Override
	public void onChatMessage(@NotNull String streamer, @NotNull String actor, @NotNull String message, @NotNull String badges){
		var event = new ChatMessageEvent(miner, TimeFactory.now(), streamer, actor, message, badges);
		miner.onEvent(event);
	}
}
