package fr.rakambda.channelpointsminer.miner.api.chat.irc;

import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.feature.twitch.TwitchSupport;
import static org.kitteh.irc.client.library.Client.Builder.Server.SecurityType.SECURE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TwitchIrcFactory{
	private static final String TWITCH_IRC_HOST = "irc.chat.twitch.tv";
    
    @NonNull
    public static Client createIrcClient(@NonNull TwitchLogin twitchLogin){
        return createIrcClient(twitchLogin.getUsername(), "oauth:%s".formatted(twitchLogin.getAccessToken()));
    }
    
	@NonNull
	private static Client createIrcClient(@NonNull String username, @Nullable String password){
		var client = Client.builder()
				.server()
				.host(TWITCH_IRC_HOST).port(443, SECURE)
				.password(password).then()
				.nick(username)
				.build();
		TwitchSupport.addSupport(client);
		return client;
	}
	
	@NonNull
	public static TwitchIrcConnectionHandler createIrcConnectionHandler(@NonNull String accountName){
		return new TwitchIrcConnectionHandler(accountName);
	}
	
	@NonNull
	public static TwitchIrcMessageHandler createIrcMessageHandler(@NonNull String accountName){
		return new TwitchIrcMessageHandler(accountName);
	}
}
