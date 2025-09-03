package fr.rakambda.channelpointsminer.miner.api.chat.irc;

import fr.rakambda.channelpointsminer.miner.log.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.engio.mbassy.listener.Handler;
import org.jspecify.annotations.NonNull;
import org.kitteh.irc.client.library.event.channel.RequestedChannelJoinCompleteEvent;
import org.kitteh.irc.client.library.event.client.ClientNegotiationCompleteEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionClosedEvent;

@RequiredArgsConstructor
@Log4j2
public class TwitchIrcConnectionHandler{
	
	@NonNull
	private final String accountName;
	
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
	public void onChannelJoinEvent(@NonNull RequestedChannelJoinCompleteEvent event){
		try(var ignored = LogContext.with(accountName)){
			log.info("Joined IRC channel {}", event.getChannel().getName());
		}
	}
}
