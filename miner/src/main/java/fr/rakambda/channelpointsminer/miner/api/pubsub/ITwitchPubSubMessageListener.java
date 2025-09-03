package fr.rakambda.channelpointsminer.miner.api.pubsub;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import org.jspecify.annotations.NonNull;

public interface ITwitchPubSubMessageListener{
	void onTwitchMessage(@NonNull Topic topic, @NonNull IPubSubMessage message);
}
