package fr.rakambda.channelpointsminer.miner.log;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
import fr.rakambda.channelpointsminer.miner.event.EventHandlerAdapter;
import fr.rakambda.channelpointsminer.miner.event.ILoggableEvent;
import fr.rakambda.channelpointsminer.miner.event.IStreamerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.function.Predicate;

@Log4j2
@RequiredArgsConstructor
public class DiscordEventListener extends EventHandlerAdapter{
	private final DiscordApi discordApi;
	private final boolean useEmbeds;
	private final Predicate<ILoggableEvent> eventFilter;
	
	@Override
	public void onILoggableEvent(@NotNull ILoggableEvent event){
		try(var context = LogContext.with(event.getMiner())){
			if(!eventFilter.test(event)){
				return;
			}
			
			if(event instanceof IStreamerEvent e){
				e.getStreamerUsername().ifPresent(context::withStreamer);
			}
			
			if(useEmbeds){
				discordApi.sendMessage(event.getAsWebhookEmbed());
			}
			else{
				discordApi.sendMessage(event.getAsWebhookMessage());
			}
		}
	}
}
