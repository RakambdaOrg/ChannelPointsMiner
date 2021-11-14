package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.IMessage;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface IMessageHandler{
	void handle(@NotNull Topic topic, @NotNull IMessage message);
}
