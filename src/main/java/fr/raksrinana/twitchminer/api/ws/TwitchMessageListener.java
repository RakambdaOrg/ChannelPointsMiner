package fr.raksrinana.twitchminer.api.ws;

import fr.raksrinana.twitchminer.api.ws.data.message.Message;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import org.jetbrains.annotations.NotNull;

public interface TwitchMessageListener{
	void onTwitchMessage(@NotNull Topic topic, @NotNull Message message);
}
