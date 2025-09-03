package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import org.jspecify.annotations.NonNull;

public interface IPubSubMessageHandler{
	void handle(@NonNull Topic topic, @NonNull IPubSubMessage message);
}
