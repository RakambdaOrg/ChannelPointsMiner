package fr.raksrinana.channelpointsminer.log;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.log.event.ILogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class DiscordLogEventListener implements ILogEventListener{
	private final DiscordApi discordApi;
	private final boolean useEmbeds;
	
	@Override
	public void onLogEvent(ILogEvent event){
		try(var ignored = LogContext.with(event.getMiner()).withStreamer(event.getStreamerUsername().orElse(null))){
			if(useEmbeds){
				discordApi.sendMessage(event.getAsWebhookEmbed());
			}
			else{
				discordApi.sendMessage(event.getAsWebhookMessage());
			}
		}
	}
}
