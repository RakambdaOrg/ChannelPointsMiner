package fr.raksrinana.channelpointsminer.log;

import fr.raksrinana.channelpointsminer.event.IEvent;
import fr.raksrinana.channelpointsminer.event.IEventListener;
import fr.raksrinana.channelpointsminer.event.ILoggableEvent;
import fr.raksrinana.channelpointsminer.event.IStreamerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class LoggerEventListener implements IEventListener{
	@Override
	public void onEvent(IEvent event){
		if(event instanceof ILoggableEvent loggableEvent){
			try(var context = LogContext.with(event.getMiner())){
				if(loggableEvent instanceof IStreamerEvent e){
					e.getStreamerUsername().ifPresent(context::withStreamer);
				}
				log.info(loggableEvent.getAsLog());
			}
		}
	}
}
