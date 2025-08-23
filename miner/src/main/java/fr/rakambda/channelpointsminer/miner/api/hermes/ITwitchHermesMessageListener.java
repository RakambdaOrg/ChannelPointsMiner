package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.IPubSubMessage;
import org.jetbrains.annotations.NotNull;

public interface ITwitchHermesMessageListener{
	void onPubSubNotification(@NotNull IPubSubMessage message);
}
