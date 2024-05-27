package fr.rakambda.channelpointsminer.miner.log.discord;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
import fr.rakambda.channelpointsminer.miner.api.discord.data.Webhook;
import fr.rakambda.channelpointsminer.miner.config.DiscordConfiguration;
import fr.rakambda.channelpointsminer.miner.config.MessageEventConfiguration;
import fr.rakambda.channelpointsminer.miner.event.EventHandlerAdapter;
import fr.rakambda.channelpointsminer.miner.event.ILoggableEvent;
import fr.rakambda.channelpointsminer.miner.event.IStreamerEvent;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
public class DiscordEventListener extends EventHandlerAdapter{
	private final DiscordApi discordApi;
	private final DiscordConfiguration discordConfiguration;
	private final DiscordMessageBuilder discordMessageBuilder;
	
	@Override
	public void onILoggableEvent(@NotNull ILoggableEvent event){
		try(var context = LogContext.with(event.getMiner())){
			if(event instanceof IStreamerEvent e){
				e.getStreamerUsername().ifPresent(context::withStreamer);
			}
			
			var eventType = event.getClass().getSimpleName();
			var config = discordConfiguration.getEvents().get(eventType);
			if(Objects.isNull(config) && !discordConfiguration.getEvents().isEmpty()){
				log.trace("Event of type {} skipped", eventType);
				return;
			}
			
			discordApi.sendMessage(buildMessage(event, config));
		}
	}
	
	@NotNull
	private Webhook buildMessage(@NotNull ILoggableEvent event, @Nullable MessageEventConfiguration config){
		if(discordConfiguration.isEmbeds()){
			return discordMessageBuilder.createEmbedMessage(event, config);
		}
		return discordMessageBuilder.createSimpleMessage(event, config);
	}
}
