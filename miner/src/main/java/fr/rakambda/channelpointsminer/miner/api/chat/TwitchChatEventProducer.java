package fr.rakambda.channelpointsminer.miner.api.chat;

import fr.rakambda.channelpointsminer.miner.event.impl.ChatMessageEvent;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class TwitchChatEventProducer implements ITwitchChatMessageListener{
	@NotNull
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
