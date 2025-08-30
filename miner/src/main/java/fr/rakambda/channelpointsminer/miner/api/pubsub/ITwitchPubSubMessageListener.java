package fr.rakambda.channelpointsminer.miner.api.pubsub;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface ITwitchPubSubMessageListener{
	void onTwitchMessage(@NotNull Topic topic, @NotNull IPubSubMessage message);
}
