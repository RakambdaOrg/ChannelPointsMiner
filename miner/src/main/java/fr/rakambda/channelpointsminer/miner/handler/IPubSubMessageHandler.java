package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface IPubSubMessageHandler{
	void handle(@NotNull Topic topic, @NotNull IPubSubMessage message);
}
