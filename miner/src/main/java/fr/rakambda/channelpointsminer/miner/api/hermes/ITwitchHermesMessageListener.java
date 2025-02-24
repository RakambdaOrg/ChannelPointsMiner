package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.hermes.data.message.IHermesMessage;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface ITwitchHermesMessageListener {
	void onTwitchMessage(@NotNull Topic topic, @NotNull IHermesMessage message);
}
