package fr.raksrinana.channelpointsminer.irc;

import lombok.extern.log4j.Log4j2;
import net.engio.mbassy.listener.Handler;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.event.channel.RequestedChannelJoinCompleteEvent;
import org.kitteh.irc.client.library.event.client.ClientNegotiationCompleteEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionClosedEvent;

@Log4j2
public class TwitchIrcEventListener{
	@Handler
	private void onClientConnectionEstablishedEvent(ClientNegotiationCompleteEvent event){
		log.info("IRC client connected");
	}
	
	@Handler
	public void onClientConnectionCLoseEvent(ClientConnectionClosedEvent event){
		if(event.canAttemptReconnect()){
			log.warn("IRC connection closed, attempting to reconnect");
			event.setAttemptReconnect(true);
		}
		else{
			log.info("IRC connection closed, cannot reconnect");
		}
	}
	
	@Handler
	public void onChannelJoinEvent(@NotNull RequestedChannelJoinCompleteEvent event){
		log.info("Joined IRC channel {}", event.getChannel().getName());
	}
}
