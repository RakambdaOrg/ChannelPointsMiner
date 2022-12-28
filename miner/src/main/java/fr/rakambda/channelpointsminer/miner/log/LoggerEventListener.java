package fr.rakambda.channelpointsminer.miner.log;

import fr.rakambda.channelpointsminer.miner.event.EventHandlerAdapter;
import fr.rakambda.channelpointsminer.miner.event.ILoggableEvent;
import fr.rakambda.channelpointsminer.miner.event.IStreamerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.jetbrains.annotations.NotNull;

@Log4j2
@RequiredArgsConstructor
public class LoggerEventListener extends EventHandlerAdapter{
	@Override
	public void onILoggableEvent(@NotNull ILoggableEvent event){
		try(var context = LogContext.with(event.getMiner())){
			if(event instanceof IStreamerEvent e){
				e.getStreamerUsername().ifPresent(context::withStreamer);
			}
			log.info(formatMessage(event, event.getConsoleLogFormat()));
		}
	}
	
	@NotNull
	private String formatMessage(@NotNull StringLookup event, @NotNull String format){
		var substitutor = new StringSubstitutor(event, "{", "}", '$');
		return substitutor.replace(format);
	}
}
