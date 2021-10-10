package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketClient;
import org.jetbrains.annotations.NotNull;

public class TwitchWebSocketClientFactory{
	@NotNull
	public static TwitchWebSocketClient createClient(){
		return new TwitchWebSocketClient();
	}
}
