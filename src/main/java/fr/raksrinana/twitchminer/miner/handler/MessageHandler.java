package fr.raksrinana.twitchminer.miner.handler;

import fr.raksrinana.twitchminer.api.ws.data.message.Message;

public interface MessageHandler<T extends Message>{
	void handle(T message);
}
