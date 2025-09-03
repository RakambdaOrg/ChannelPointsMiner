package fr.rakambda.channelpointsminer.miner.log.telegram;

import fr.rakambda.channelpointsminer.miner.api.telegram.TelegramApi;
import fr.rakambda.channelpointsminer.miner.api.telegram.data.Message;
import fr.rakambda.channelpointsminer.miner.config.MessageEventConfiguration;
import fr.rakambda.channelpointsminer.miner.config.TelegramConfiguration;
import fr.rakambda.channelpointsminer.miner.event.EventHandlerAdapter;
import fr.rakambda.channelpointsminer.miner.event.ILoggableEvent;
import fr.rakambda.channelpointsminer.miner.event.IStreamerEvent;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
public class TelegramEventListener extends EventHandlerAdapter{
	private final TelegramApi telegramApi;
	private final TelegramConfiguration telegramConfiguration;
	private final TelegramMessageBuilder telegramMessageBuilder;
	
	@Override
	public void onILoggableEvent(@NonNull ILoggableEvent event){
		try(var context = LogContext.with(event.getMiner())){
			if(event instanceof IStreamerEvent e){
				e.getStreamerUsername().ifPresent(context::withStreamer);
			}
			
			var eventType = event.getClass().getSimpleName();
			var config = telegramConfiguration.getEvents().get(eventType);
			if(Objects.isNull(config) && !telegramConfiguration.getEvents().isEmpty()){
				log.trace("Event of type {} skipped", eventType);
				return;
			}
			
			telegramApi.sendMessage(buildMessage(event, config));
		}
	}
	
	@NonNull
	private Message buildMessage(@NonNull ILoggableEvent event, @Nullable MessageEventConfiguration config){
		return telegramMessageBuilder.createSimpleMessage(event, config, telegramConfiguration.getChatId());
	}
}
