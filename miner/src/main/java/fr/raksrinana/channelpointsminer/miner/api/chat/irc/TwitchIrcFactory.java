package fr.raksrinana.channelpointsminer.miner.api.chat.irc;

import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.feature.twitch.TwitchSupport;
import static org.kitteh.irc.client.library.Client.Builder.Server.SecurityType.SECURE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TwitchIrcFactory{
	private static final String TWITCH_IRC_HOST = "irc.chat.twitch.tv";
    
    @NotNull
    public static Client createIrcClient(@NotNull TwitchLogin twitchLogin){
        return createIrcClient(twitchLogin.getUsername(), "oauth:%s".formatted(twitchLogin.getAccessToken()));
    }
    
	@NotNull
	private static Client createIrcClient(@NotNull String username, @Nullable String password){
		var client = Client.builder()
				.server()
				.host(TWITCH_IRC_HOST).port(443, SECURE)
				.password(password).then()
				.nick(username)
				.build();
		TwitchSupport.addSupport(client);
		return client;
	}
}
