package fr.raksrinana.channelpointsminer.miner.irc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.MessageTag;
import org.kitteh.irc.client.library.util.TriFunction;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
public class TwitchIrcClientPrototype{
    
    @NotNull
    private final List<String> capabilities = new LinkedList<>();
    @NotNull
    private final List<Object> handlers = new LinkedList<>();
    @NotNull
    private final List<TagCreator> tagCreators = new LinkedList<>();
    
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
    
    public TwitchIrcClientPrototype addCapability(@NotNull String cap){
        capabilities.add(cap);
        return this;
    }
    
    public final TwitchIrcClientPrototype addTagCreator(@NotNull String cap, @NotNull String tagName, @NotNull TriFunction<Client, String, String, ? extends MessageTag> creator){
        tagCreators.add(new TagCreator(cap, tagName, creator));
        return this;
    }
    
    public TwitchIrcClientPrototype addIrcHandler(@NotNull Object handler){
        handlers.add(handler);
        return this;
    }
}
