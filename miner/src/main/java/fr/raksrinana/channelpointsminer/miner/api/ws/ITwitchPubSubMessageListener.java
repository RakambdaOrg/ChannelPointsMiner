package fr.raksrinana.channelpointsminer.miner.api.ws;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.IMessage;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface ITwitchPubSubMessageListener{
	void onTwitchMessage(@NotNull Topic topic, @NotNull IMessage message);
}
