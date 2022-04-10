package fr.raksrinana.channelpointsminer.miner.api.twitch.data;

import java.util.Collection;

public interface IPlayerEventRequest<T extends PlayerEvent>{
	Collection<T> getData();
}
