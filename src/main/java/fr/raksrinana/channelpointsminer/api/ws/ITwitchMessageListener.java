package fr.raksrinana.channelpointsminer.api.ws;

import fr.raksrinana.channelpointsminer.api.ws.data.message.IMessage;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface ITwitchMessageListener{
	void onTwitchMessage(@NotNull Topic topic, @NotNull IMessage message);
}
