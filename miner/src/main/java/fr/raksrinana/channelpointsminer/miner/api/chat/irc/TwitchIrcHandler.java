package fr.raksrinana.channelpointsminer.miner.api.chat.irc;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatMessageListener;
import fr.raksrinana.channelpointsminer.miner.log.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.engio.mbassy.listener.Handler;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.channel.RequestedChannelJoinCompleteEvent;
import org.kitteh.irc.client.library.event.client.ClientNegotiationCompleteEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionClosedEvent;
import java.util.List;

@RequiredArgsConstructor
@Log4j2
public class TwitchIrcHandler{
    
    @NotNull
	private final String accountName;
    
    @NotNull
    private List<ITwitchChatMessageListener> messageListeners;
	
	@Handler
	private void onClientConnectionEstablishedEvent(ClientNegotiationCompleteEvent event){
		try(var ignored = LogContext.with(accountName)){
			log.info("IRC client connected");
		}
	}

	@Handler
	public void onClientConnectionCLoseEvent(ClientConnectionClosedEvent event){
		try(var ignored = LogContext.with(accountName)){
			if(event.canAttemptReconnect()){
				log.warn("IRC connection closed, attempting to reconnect");
				event.setAttemptReconnect(true);
			}
			else{
				log.info("IRC connection closed, cannot reconnect");
			}
		}
	}

	@Handler
	public void onChannelJoinEvent(@NotNull RequestedChannelJoinCompleteEvent event){
		try(var ignored = LogContext.with(accountName)){
			log.info("Joined IRC channel {}", event.getChannel().getName());
		}
	}
    
    @Handler
    public void onMessageEvent(@NotNull ChannelMessageEvent event) {
        try  (var ignored = LogContext.with(accountName)) {
            log.trace("Received Irc Chat Message");
            var badges = event.getTag("badges");
            if(badges.isPresent()){
                messageListeners.forEach(l -> l.processMessage(
                        event.getChannel().getName().substring(1),
                        event.getActor().getMessagingName(),
                        event.getMessage(),
                        badges.get().getAsString()));
            }
            else {
                messageListeners.forEach(l -> l.processMessage(
                        event.getChannel().getName().substring(1),
                        event.getActor().getMessagingName(),
                        event.getMessage()));
            }
        }
    }
}
