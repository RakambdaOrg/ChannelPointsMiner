package fr.raksrinana.channelpointsminer.miner.handler;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.IMessage;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface IMessageHandler{
	void handle(@NotNull Topic topic, @NotNull IMessage message);
}
