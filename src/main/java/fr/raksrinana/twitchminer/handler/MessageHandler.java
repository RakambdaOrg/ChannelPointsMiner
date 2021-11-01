package fr.raksrinana.twitchminer.handler;

import fr.raksrinana.twitchminer.api.ws.data.message.Message;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface MessageHandler{
	void handle(@NotNull Topic topic, @NotNull Message message);
}
