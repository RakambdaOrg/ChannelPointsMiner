package fr.rakambda.channelpointsminer.miner.api.chat.irc;

import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatMessageListener;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.engio.mbassy.listener.Handler;
import org.jspecify.annotations.NonNull;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import java.util.Collection;
import java.util.LinkedList;

@RequiredArgsConstructor
@Log4j2
public class TwitchIrcMessageHandler{
	
	@NonNull
	private final String accountName;
	
	@NonNull
	private final Collection<ITwitchChatMessageListener> listeners = new LinkedList<>();
	
	@Handler
	public void onMessageEvent(@NonNull ChannelMessageEvent event){
		try(var ignored = LogContext.with(accountName)){
			log.trace("Received Irc Chat Message");
			var badges = event.getTag("badges");
			if(badges.isPresent()){
				listeners.forEach(l -> l.onChatMessage(
						event.getChannel().getName().substring(1),
						event.getActor().getMessagingName(),
						event.getMessage(),
						badges.get().getAsString()));
			}
			else{
				listeners.forEach(l -> l.onChatMessage(
						event.getChannel().getName().substring(1),
						event.getActor().getMessagingName(),
						event.getMessage()));
			}
		}
	}
	
	public void addListener(ITwitchChatMessageListener listener){
		listeners.add(listener);
	}
}
