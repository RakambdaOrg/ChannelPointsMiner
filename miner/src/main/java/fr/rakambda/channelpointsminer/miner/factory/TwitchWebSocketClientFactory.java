package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketClient;
import fr.rakambda.channelpointsminer.miner.api.hermes.TwitchHermesWebSocketClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.pubsub.TwitchPubSubWebSocketClient;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import java.net.URI;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TwitchWebSocketClientFactory{
	private static final URI PUB_SUB_URI = URI.create("wss://pubsub-edge.twitch.tv/v1");
	private static final String HERMES_URI_BASE = "wss://hermes.twitch.tv/v1";
	private static final URI IRC_URI = URI.create("wss://irc-ws.chat.twitch.tv/");
	
	@NonNull
	public static TwitchPubSubWebSocketClient createPubSubClient(){
		return new TwitchPubSubWebSocketClient(PUB_SUB_URI);
	}
	
	@NonNull
	public static TwitchHermesWebSocketClient createHermesClient(@NonNull IEventManager eventManager){
		return new TwitchHermesWebSocketClient(URI.create("%s?clientId=%s".formatted(HERMES_URI_BASE, TwitchClient.WEB.getClientId())), eventManager);
	}
	
	@NonNull
	public static TwitchHermesWebSocketClient createHermesClient(@NonNull String reconnectUrl, @NonNull IEventManager eventManager){
		return new TwitchHermesWebSocketClient(URI.create(reconnectUrl), eventManager);
	}
	
	@NonNull
	public static TwitchChatWebSocketClient createChatClient(@NonNull TwitchLogin twitchLogin, boolean listenMessages){
		return new TwitchChatWebSocketClient(IRC_URI, twitchLogin, listenMessages);
	}
}
