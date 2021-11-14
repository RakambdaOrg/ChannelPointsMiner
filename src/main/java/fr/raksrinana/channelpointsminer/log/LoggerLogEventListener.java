package fr.raksrinana.channelpointsminer.log;

import fr.raksrinana.channelpointsminer.log.event.ILogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class LoggerLogEventListener implements ILogEventListener{
	@Override
	public void onLogEvent(ILogEvent event){
		try(var ignored = LogContext.with(event.getMiner()).withStreamer(event.getStreamer().orElse(null))){
			log.info(event.getAsLog());
		}
	}
}
