package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketClient;
import fr.rakambda.channelpointsminer.miner.api.hermes.TwitchHermesWebSocketClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.pubsub.TwitchPubSubWebSocketClient;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.net.URI;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TwitchWebSocketClientFactory{
	private static final URI PUB_SUB_URI = URI.create("wss://pubsub-edge.twitch.tv/v1");
	private static final URI HERMES_URI = URI.create("wss://hermes.twitch.tv/v1?clientId=kimne78kx3ncx6brgo4mv6wki5h1k");
	private static final URI IRC_URI = URI.create("wss://irc-ws.chat.twitch.tv/");
	
	@NotNull
	public static TwitchPubSubWebSocketClient createPubSubClient(){
		return new TwitchPubSubWebSocketClient(PUB_SUB_URI);
	}
	@NotNull
	public static TwitchHermesWebSocketClient createHermesClient(){
		return new TwitchHermesWebSocketClient(HERMES_URI);
	}
	
	@NotNull
	public static TwitchChatWebSocketClient createChatClient(@NotNull TwitchLogin twitchLogin, boolean listenMessages){
		return new TwitchChatWebSocketClient(IRC_URI, twitchLogin, listenMessages);
	}
}
