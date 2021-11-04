package fr.raksrinana.channelpointsminer.irc;

import fr.raksrinana.channelpointsminer.api.passport.TwitchLogin;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.Client;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
public class TwitchIrcClient implements AutoCloseable{
	
	@NotNull
	private final TwitchLogin twitchLogin;
	
	@Nullable
	private Client ircClient;
	
	public void join(@NotNull String channel){
		var client = getIrcClient();
		var ircChannelName = "#%s".formatted(channel.toLowerCase(Locale.ROOT));
		
		log.info("Joining IRC channel {}", ircChannelName);
		client.addChannel(ircChannelName);
	}
	
	public void leave(@NotNull String channel){
		if(Objects.isNull(ircClient)){
			log.debug("Didn't leave irc channel #{channel} as no connection has been made");
			return;
		}
		
		var ircChannelName = "#%s".formatted(channel);
		
		log.info("Leaving IRC channel {}", ircChannelName);
		ircClient.removeChannel(ircChannelName);
	}
	
	private synchronized Client getIrcClient(){
		if(Objects.isNull(ircClient)){
			log.info("Creating new Twitch IRC client");
			
			ircClient = TwitchIrcFactory.createClient(twitchLogin);
			ircClient.connect();
			ircClient.setExceptionListener(e -> log.error("Error from irc", e));
			
			ircClient.getEventManager().registerEventListener(TwitchIrcFactory.createListener());
			
			log.info("IRC Client created");
		}
		
		return ircClient;
	}
	
	public void close(){
		Optional.ofNullable(ircClient).ifPresent(Client::shutdown);
	}
}
