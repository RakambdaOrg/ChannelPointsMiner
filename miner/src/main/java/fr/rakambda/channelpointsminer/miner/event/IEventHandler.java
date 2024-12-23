package fr.rakambda.channelpointsminer.miner.event;

import java.io.IOException;

public interface IEventHandler extends AutoCloseable{
	void onEvent(IEvent event);
	
	@Override
	default void close() throws IOException{}
}
