package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketClient;
import fr.rakambda.channelpointsminer.miner.api.hermes.TwitchHermesWebSocketClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.pubsub.TwitchPubSubWebSocketClient;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.net.URI;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TwitchWebSocketClientFactory{
	private static final URI PUB_SUB_URI = URI.create("wss://pubsub-edge.twitch.tv/v1");
	private static final String HERMES_URI_BASE = "wss://hermes.twitch.tv/v1";
	private static final URI IRC_URI = URI.create("wss://irc-ws.chat.twitch.tv/");
	
	@NotNull
	public static TwitchPubSubWebSocketClient createPubSubClient(){
		return new TwitchPubSubWebSocketClient(PUB_SUB_URI);
	}
	
	@NotNull
	public static TwitchHermesWebSocketClient createHermesClient(@NotNull IEventManager eventManager){
		return new TwitchHermesWebSocketClient(URI.create("%s?clientId=%s".formatted(HERMES_URI_BASE, TwitchClient.WEB.getClientId())), eventManager);
	}
	
	@NotNull
	public static TwitchHermesWebSocketClient createHermesClient(@NotNull String reconnectUrl, @NotNull IEventManager eventManager){
		return new TwitchHermesWebSocketClient(URI.create(reconnectUrl), eventManager);
	}
	
	@NotNull
	public static TwitchChatWebSocketClient createChatClient(@NotNull TwitchLogin twitchLogin, boolean listenMessages){
		return new TwitchChatWebSocketClient(IRC_URI, twitchLogin, listenMessages);
	}
}
