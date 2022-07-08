package fr.raksrinana.channelpointsminer.miner.irc.listeners;

import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.log.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.engio.mbassy.listener.Handler;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import java.sql.SQLException;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Log4j2
public class TwitchIrcMessageEventListener{
    
    @NotNull
    private final IDatabase database;
    
    private final String accountName;
    
    private final static Pattern predPatt = Pattern.compile("predictions/([^,]*)");
    
    @Handler
    public void onMessageEvent(@NotNull ChannelMessageEvent event) {
        try  (var ignored = LogContext.with(accountName)) {
            log.trace("Received Chat Message");
            event.getTag("badges").ifPresent((b) -> {
                var m = predPatt.matcher(b.getAsString());
                if(m.find()){
                    try{
                        log.debug("Read user prediction from chat. User {}, Badge: {}", event.getActor().getMessagingName(), m.group(1));
                        database.addUserPrediction(event.getActor().getMessagingName(), event.getChannel().getName().substring(1), m.group(1));
                    }
                    catch(SQLException e){
                        log.error("SQL Exception while adding user prediction: {}", e.getMessage());
                    }
                }
            });
        }
    }
}
