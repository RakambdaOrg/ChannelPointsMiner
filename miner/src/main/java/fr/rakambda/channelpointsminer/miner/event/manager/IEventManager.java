package fr.rakambda.channelpointsminer.miner.event.manager;

import fr.rakambda.channelpointsminer.miner.event.IEvent;
import fr.rakambda.channelpointsminer.miner.event.IEventHandler;
import org.jetbrains.annotations.NotNull;

public interface IEventManager extends AutoCloseable {
	void addEventHandler(@NotNull IEventHandler handler);

	void onEvent(@NotNull IEvent event);

	@Override
	void close();
}
