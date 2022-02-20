package fr.raksrinana.channelpointsminer.event;

public interface IEventListener extends AutoCloseable{
	void onEvent(IEvent event);
	
	@Override
	default void close(){}
}
