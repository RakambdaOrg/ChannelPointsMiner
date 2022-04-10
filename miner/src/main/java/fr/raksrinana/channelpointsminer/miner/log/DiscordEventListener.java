package fr.raksrinana.channelpointsminer.miner.log;

import fr.raksrinana.channelpointsminer.miner.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.miner.event.IEvent;
import fr.raksrinana.channelpointsminer.miner.event.IEventListener;
import fr.raksrinana.channelpointsminer.miner.event.ILoggableEvent;
import fr.raksrinana.channelpointsminer.miner.event.IStreamerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class DiscordEventListener implements IEventListener{
	private final DiscordApi discordApi;
	private final boolean useEmbeds;
	
	@Override
	public void onEvent(IEvent event){
		if(event instanceof ILoggableEvent loggableEvent){
			try(var context = LogContext.with(event.getMiner())){
				if(loggableEvent instanceof IStreamerEvent e){
					e.getStreamerUsername().ifPresent(context::withStreamer);
				}
				
				if(useEmbeds){
					discordApi.sendMessage(loggableEvent.getAsWebhookEmbed());
				}
				else{
					discordApi.sendMessage(loggableEvent.getAsWebhookMessage());
				}
			}
		}
	}
}
