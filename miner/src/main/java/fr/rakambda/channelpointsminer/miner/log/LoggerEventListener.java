package fr.rakambda.channelpointsminer.miner.log;

import fr.rakambda.channelpointsminer.miner.event.EventHandlerAdapter;
import fr.rakambda.channelpointsminer.miner.event.ILoggableEvent;
import fr.rakambda.channelpointsminer.miner.event.IStreamerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.jspecify.annotations.NonNull;

@Log4j2
@RequiredArgsConstructor
public class LoggerEventListener extends EventHandlerAdapter{
	@Override
	public void onILoggableEvent(@NonNull ILoggableEvent event){
		try(var context = LogContext.with(event.getMiner())){
			if(event instanceof IStreamerEvent e){
				e.getStreamerUsername().ifPresent(context::withStreamer);
			}
			log.info(formatMessage(event, event.getConsoleLogFormat()));
		}
	}
	
	@NonNull
	private String formatMessage(@NonNull StringLookup event, @NonNull String format){
		var substitutor = new StringSubstitutor(event, "{", "}", '$');
		return substitutor.replace(format);
	}
}
