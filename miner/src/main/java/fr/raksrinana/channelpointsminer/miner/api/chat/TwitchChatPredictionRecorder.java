package fr.raksrinana.channelpointsminer.miner.api.chat;

import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.sql.SQLException;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Log4j2
public class TwitchChatPredictionRecorder implements ITwitchChatMessageListener{
    
    @NotNull
    private final IDatabase database;
    
    private final static Pattern predPatt = Pattern.compile("predictions/([^,]*)");
    
    @Override
    public void processMessage(String streamer, String actor, String message){
    
    }
    
    @Override
    public void processMessage(@NotNull String streamer, @NotNull String actor, @NotNull String message, @NotNull String badges){
        var m = predPatt.matcher(badges);
        if(m.find()){
            try{
                log.debug("Read user prediction from chat. User: {}, Badge: {}", actor, m.group(1));
                database.addUserPrediction(actor, streamer, m.group(1));
            }
            catch(SQLException e){
                log.error("SQL Exception while adding user prediction: {}", e.getMessage());
            }
        }
    }
    
}
