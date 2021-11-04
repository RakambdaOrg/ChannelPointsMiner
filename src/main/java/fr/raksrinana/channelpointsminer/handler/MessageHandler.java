package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.Message;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface MessageHandler{
	void handle(@NotNull Topic topic, @NotNull Message message);
}
