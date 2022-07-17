package fr.raksrinana.channelpointsminer.miner.api.chat;

import fr.raksrinana.channelpointsminer.miner.api.chat.irc.TwitchIrcChatClient;
import fr.raksrinana.channelpointsminer.miner.api.chat.irc.TwitchIrcEventListener;
import fr.raksrinana.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.config.ChatMode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.feature.twitch.TwitchSupport;
import static org.kitteh.irc.client.library.Client.Builder.Server.SecurityType.SECURE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TwitchChatFactory{
	private static final String TWITCH_IRC_HOST = "irc.chat.twitch.tv";
	
	@NotNull
	public static ITwitchChatClient createChat(@NotNull ChatMode chatMode, @NotNull TwitchLogin twitchLogin){
		return switch(chatMode){
			case IRC -> createIrcChat(twitchLogin);
			case WS -> createWsChat(twitchLogin);
		};
	}
	
	@NotNull
	private static ITwitchChatClient createIrcChat(@NotNull TwitchLogin twitchLogin){
		return new TwitchIrcChatClient(twitchLogin);
	}
	
	@NotNull
	private static ITwitchChatClient createWsChat(@NotNull TwitchLogin twitchLogin){
		return new TwitchChatWebSocketPool(Integer.MAX_VALUE, twitchLogin);
	}
	
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
	
	@NotNull
	public static TwitchIrcEventListener createIrcListener(@NotNull String accountName){
		return new TwitchIrcEventListener(accountName);
	}
}
