package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketClient;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.net.URI;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TwitchWebSocketClientFactory{
	private static final URI WEBSOCKET_URI = URI.create("wss://pubsub-edge.twitch.tv/v1");
	
	@NotNull
	public static TwitchWebSocketClient createClient(){
		return new TwitchWebSocketClient(WEBSOCKET_URI);
	}
}
