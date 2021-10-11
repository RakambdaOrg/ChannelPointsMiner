package fr.raksrinana.twitchminer.api.twitch.data;

import java.util.Collection;

public interface PlayerEventRequest<T extends PlayerEvent>{
	Collection<T> getData();
}
