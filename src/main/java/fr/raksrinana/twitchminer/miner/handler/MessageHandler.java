package fr.raksrinana.twitchminer.miner.handler;

import fr.raksrinana.twitchminer.api.ws.data.message.Message;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface MessageHandler<T extends Message>{
	void handle(@NotNull Topic topic, @NotNull T message);
}
