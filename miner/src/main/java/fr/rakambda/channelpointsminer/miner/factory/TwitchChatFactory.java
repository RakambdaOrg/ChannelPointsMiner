package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.rakambda.channelpointsminer.miner.api.chat.irc.TwitchIrcChatClient;
import fr.rakambda.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketPool;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.config.ChatMode;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import org.jspecify.annotations.NonNull;

public class TwitchChatFactory{
	@NonNull
	public static ITwitchChatClient createChat(@NonNull IMiner miner, @NonNull ChatMode chatMode, boolean listenMessages){
		var twitchLogin = miner.getTwitchLogin();
		
		return switch(chatMode){
			case IRC -> createIrcChat(twitchLogin, listenMessages);
			case WS -> createWsChat(twitchLogin, listenMessages);
		};
	}
	
	@NonNull
	private static ITwitchChatClient createIrcChat(@NonNull TwitchLogin twitchLogin, boolean listenMessages){
		return new TwitchIrcChatClient(twitchLogin, listenMessages);
	}
	
	@NonNull
	private static ITwitchChatClient createWsChat(@NonNull TwitchLogin twitchLogin, boolean listenMessages){
		return new TwitchChatWebSocketPool(Integer.MAX_VALUE, twitchLogin, listenMessages);
	}
}
