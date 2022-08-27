package fr.raksrinana.channelpointsminer.miner.api.chat.irc;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatMessageListener;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.defaults.element.messagetag.DefaultMessageTagLabel;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
public class TwitchIrcChatClient implements ITwitchChatClient{
	
	private static final String TAGS_CAPABILITY = "twitch.tv/tags";
	private static final String EMOTE_SETS_TAG_NAME = "emote-sets";
	
	@NotNull
	private final TwitchLogin twitchLogin;
	private final boolean listenMessages;
	private final Collection<ITwitchChatMessageListener> chatMessageListeners = new LinkedList<>();
	
	@Nullable
	private Client ircClient;
	@Nullable
	private TwitchIrcMessageHandler ircMessageHandler;
	
	@Override
	public void join(@NotNull String channel){
		var client = getIrcClient();
		var ircChannelName = "#%s".formatted(channel.toLowerCase(Locale.ROOT));
		if(client.getChannel(ircChannelName).isPresent()){
			log.trace("Tried to join IRC channel {} that is already joined", ircChannelName);
			return;
		}
		
		log.info("Joining IRC channel {}", ircChannelName);
		client.addChannel(ircChannelName);
	}
	
	@Override
	public void joinPending(){
	}
	
	@Override
	public void addChatMessageListener(@NotNull ITwitchChatMessageListener listener){
		chatMessageListeners.add(listener);
		Optional.ofNullable(ircMessageHandler).ifPresent(i -> i.addListener(listener));
	}
	
	@Override
	public void leave(@NotNull String channel){
		if(Objects.isNull(ircClient)){
			log.debug("Didn't leave irc channel #{} as no connection has been made", channel);
			return;
		}
		
		var ircChannelName = "#%s".formatted(channel);
		if(ircClient.getChannel(ircChannelName).isEmpty()){
			log.trace("Tried to leave IRC channel {} that is not joined", ircChannelName);
			return;
		}
		
		log.info("Leaving IRC channel {}", ircChannelName);
		ircClient.removeChannel(ircChannelName);
	}
	
	@Override
	public void ping(){
	}
	
	@Override
	public void close(){
		Optional.ofNullable(ircClient).ifPresent(Client::shutdown);
	}
	
	@NotNull
	private synchronized Client getIrcClient(){
		if(Objects.isNull(ircClient)){
			log.info("Creating new Twitch IRC client");
			
			ircClient = TwitchIrcFactory.createIrcClient(twitchLogin);
			ircClient.connect();
			ircClient.setExceptionListener(e -> log.error("Error from irc", e));
			
			var eventManager = ircClient.getEventManager();
			eventManager.registerEventListener(TwitchIrcFactory.createIrcConnectionHandler(twitchLogin.getUsername()));
			
			if(listenMessages){
				ircMessageHandler = TwitchIrcFactory.createIrcMessageHandler(twitchLogin.getUsername());
				chatMessageListeners.forEach(ircMessageHandler::addListener);
				eventManager.registerEventListener(ircMessageHandler);
				
				var capabilityRequest = ircClient.commands().capabilityRequest();
				capabilityRequest.enable(TAGS_CAPABILITY);
				capabilityRequest.execute();
				
				var tagManager = ircClient.getMessageTagManager();
				tagManager.registerTagCreator(TAGS_CAPABILITY, EMOTE_SETS_TAG_NAME, DefaultMessageTagLabel.FUNCTION);
			}
			
			log.info("IRC Client created");
		}
		
		return ircClient;
	}
}
