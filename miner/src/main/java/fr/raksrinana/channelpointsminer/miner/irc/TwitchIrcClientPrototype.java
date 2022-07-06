package fr.raksrinana.channelpointsminer.miner.irc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
public class TwitchIrcClientPrototype{
    
    
    private final List<String> capabilities = new LinkedList<>();
    private final List<Object> handlers = new LinkedList<>();
    
    public TwitchIrcClientPrototype addCapability(@NotNull String cap){
        capabilities.add(cap);
        return this;
    }
    
    public TwitchIrcClientPrototype addIrcHandler(@NotNull Object handler){
        handlers.add(handler);
        return this;
    }
}
