package fr.raksrinana.channelpointsminer.miner.event;

public interface IEventListener extends AutoCloseable{
	void onEvent(IEvent event);
	
	@Override
	default void close(){}
}
