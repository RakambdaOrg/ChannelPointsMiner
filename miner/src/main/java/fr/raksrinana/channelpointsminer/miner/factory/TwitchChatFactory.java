package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.raksrinana.channelpointsminer.miner.api.chat.irc.TwitchIrcChatClient;
import fr.raksrinana.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.config.ChatMode;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import org.jetbrains.annotations.NotNull;

public class TwitchChatFactory{
	@NotNull
	public static ITwitchChatClient createChat(@NotNull IMiner miner, @NotNull ChatMode chatMode, boolean listenMessages){
		var twitchLogin = miner.getTwitchLogin();
		
		var chatClient = switch(chatMode){
			case IRC -> createIrcChat(twitchLogin, listenMessages);
			case WS -> createWsChat(twitchLogin, listenMessages);
		};
		
		return chatClient;
	}
	
	@NotNull
	private static ITwitchChatClient createIrcChat(@NotNull TwitchLogin twitchLogin, boolean listenMessages){
		return new TwitchIrcChatClient(twitchLogin, listenMessages);
	}
	
	@NotNull
	private static ITwitchChatClient createWsChat(@NotNull TwitchLogin twitchLogin, boolean listenMessages){
		return new TwitchChatWebSocketPool(Integer.MAX_VALUE, twitchLogin, listenMessages);
	}
}
