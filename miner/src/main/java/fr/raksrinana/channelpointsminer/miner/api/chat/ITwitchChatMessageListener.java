package fr.raksrinana.channelpointsminer.miner.api.chat;

public interface ITwitchChatMessageListener{

    void processMessage(String streamer, String actor, String message);
    
    void processMessage(String streamer, String actor, String message, String badges);
}
