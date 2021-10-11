package fr.raksrinana.twitchminer.api.ws;

import fr.raksrinana.twitchminer.api.ws.data.message.Message;
import org.jetbrains.annotations.NotNull;

public interface TwitchMessageListener{
	void onTwitchMessage(@NotNull Message message);
}
