package fr.rakambda.channelpointsminer.miner.event.manager;

import fr.rakambda.channelpointsminer.miner.event.IEvent;
import fr.rakambda.channelpointsminer.miner.event.IEventHandler;
import org.jspecify.annotations.NonNull;

public interface IEventManager extends AutoCloseable {
	void addEventHandler(@NonNull IEventHandler handler);

	void onEvent(@NonNull IEvent event);

	@Override
	void close();
}
