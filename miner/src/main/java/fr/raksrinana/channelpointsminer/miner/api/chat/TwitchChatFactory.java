package fr.raksrinana.channelpointsminer.miner.api.chat;

import fr.raksrinana.channelpointsminer.miner.api.chat.irc.TwitchIrcChatClient;
import fr.raksrinana.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.config.ChatMode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TwitchChatFactory{
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
}
