package fr.rakambda.channelpointsminer.miner.event.manager;

import com.google.common.annotations.VisibleForTesting;
import fr.rakambda.channelpointsminer.miner.event.IEvent;
import fr.rakambda.channelpointsminer.miner.event.IEventHandler;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.jspecify.annotations.NonNull;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

@Log4j2
@RequiredArgsConstructor
public class EventManager implements IEventManager{
	@Getter(value = AccessLevel.PUBLIC)
	private final Collection<IEventHandler> eventHandlers = new ConcurrentLinkedQueue<>();
	
	private final ExecutorService handlerExecutor;
	
	@Setter
	private IMiner miner;
	
	@Override
	public void addEventHandler(@NonNull IEventHandler handler){
		eventHandlers.add(handler);
	}
	
	@Override
	public void onEvent(@NonNull IEvent event){
		event.setMiner(miner);
		
		var values = ThreadContext.getImmutableContext();
		var messages = ThreadContext.getImmutableStack().asList();
		
		eventHandlers.forEach(listener -> handlerExecutor.submit(() -> {
			try(var ignored = LogContext.restore(values, messages)){
				listener.onEvent(event);
			}
		}));
	}
	
	@Override
	public void close(){
		for(var listener : eventHandlers){
			try{
				listener.close();
			}
			catch(IOException e){
				log.error("Failed to close event handler {}", listener, e);
			}
		}
	}
}
