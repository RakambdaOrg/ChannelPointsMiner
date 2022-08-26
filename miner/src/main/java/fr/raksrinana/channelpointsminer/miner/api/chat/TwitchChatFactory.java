package fr.raksrinana.channelpointsminer.miner.api.chat;

import fr.raksrinana.channelpointsminer.miner.api.chat.irc.TwitchIrcChatClient;
import fr.raksrinana.channelpointsminer.miner.api.chat.ws.TwitchChatWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.config.ChatMode;
import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.defaults.element.messagetag.DefaultMessageTagLabel;
import org.kitteh.irc.client.library.element.MessageTag;
import org.kitteh.irc.client.library.util.TriFunction;
import java.util.LinkedList;
import java.util.List;

public class TwitchChatFactory{
    
    private final boolean recordPredictions;

    private final IDatabase database;
    
    public TwitchChatFactory(){
        recordPredictions = false;
        database = null;
    }
    
    public TwitchChatFactory(IDatabase databaseForPredictionRecording){
        recordPredictions = true;
        database = databaseForPredictionRecording;
    }
    
    @AllArgsConstructor
    @Getter
    public static class TagCreator{
        @NotNull
        private String capability;
        @NotNull
        private String tagName;
        @NotNull
        private TriFunction<Client, String, String, ? extends MessageTag> tagCreator;
    }
    
	@NotNull
	public ITwitchChatClient createChat(@NotNull ChatMode chatMode, @NotNull TwitchLogin twitchLogin){
        return switch(chatMode){
            case IRC -> createIrcChat(twitchLogin);
            case WS -> createWsChat(twitchLogin);
        };
	}
	
    @NotNull
    private ITwitchChatClient createIrcChat(@NotNull TwitchLogin twitchLogin){
        List<String> capabilities = new LinkedList<>();
        List<ITwitchChatMessageListener> chatMessageListeners = new LinkedList<>();
        List<TagCreator> tagCreators = new LinkedList<>();
        
        tagCreators.add(new TagCreator("twitch.tv/tags", "emote-sets", DefaultMessageTagLabel.FUNCTION));
        if(recordPredictions && database != null){
            chatMessageListeners.add(new TwitchChatPredictionRecorder(database));
            capabilities.add("twitch.tv/tags");
        }
        return new TwitchIrcChatClient(twitchLogin, capabilities, chatMessageListeners, tagCreators);
    }
	
	@NotNull
	private ITwitchChatClient createWsChat(@NotNull TwitchLogin twitchLogin){
        List<ITwitchChatMessageListener> chatMessageListeners = new LinkedList<>();
        if(recordPredictions && database != null){
            chatMessageListeners.add(new TwitchChatPredictionRecorder(database));
        }
		return new TwitchChatWebSocketPool(Integer.MAX_VALUE, twitchLogin, chatMessageListeners);
	}
}
