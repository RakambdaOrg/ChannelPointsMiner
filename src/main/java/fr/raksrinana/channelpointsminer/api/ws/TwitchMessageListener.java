package fr.raksrinana.channelpointsminer.api.ws;

import fr.raksrinana.channelpointsminer.api.ws.data.message.Message;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface TwitchMessageListener{
	void onTwitchMessage(@NotNull Topic topic, @NotNull Message message);
}
