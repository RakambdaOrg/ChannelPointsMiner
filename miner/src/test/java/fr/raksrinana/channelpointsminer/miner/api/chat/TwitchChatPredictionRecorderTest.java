package fr.raksrinana.channelpointsminer.miner.api.chat;

import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class TwitchChatPredictionRecorderTest{
    
    private final static String STREAMER_NAME = "channel";
    private final static String ACTOR = "username";
    private final static String MESSAGE = "message";
    private final static String BADGE_PREDICTION_INFO = "badges=predictions/color,sub";
    private final static String BADGE_NO_PREDICTION_INFO = "badges=sub";
    private final static String PREDICTION = "color";
    
    
    @InjectMocks
    private TwitchChatPredictionRecorder tested;
    
    @Mock
    private IDatabase database;
    
    
    @Test
    void predictionRecorded() throws SQLException{
        assertDoesNotThrow(() -> tested.processMessage(STREAMER_NAME, ACTOR, MESSAGE, BADGE_PREDICTION_INFO));
        
        verify(database).addUserPrediction(ACTOR, STREAMER_NAME, PREDICTION);
    }
    
    @Test
    void noPredictionRecorded() throws SQLException{
        assertDoesNotThrow(() -> tested.processMessage(STREAMER_NAME, ACTOR, MESSAGE, BADGE_NO_PREDICTION_INFO));
        
        verify(database, never()).addUserPrediction(any(), any(), any());
    }
    
    @Test
    void noBadgeFound() throws SQLException{
        assertDoesNotThrow(() -> tested.processMessage(STREAMER_NAME, ACTOR, MESSAGE));
        
        verify(database, never()).addUserPrediction(any(), any(), any());
    }
}