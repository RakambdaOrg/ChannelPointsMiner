package fr.raksrinana.channelpointsminer.miner.database;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.pointsearned.Balance;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.pointsspent.PointsSpentData;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.predictionresult.PredictionResultData;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Badge;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.EventStatus;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.PointGain;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.PointReasonCode;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Prediction;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Predictor;
import fr.raksrinana.channelpointsminer.miner.event.impl.EventUpdatedEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PointsEarnedEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PointsSpentEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PredictionMadeEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PredictionResultEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamDownEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamUpEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.StreamerAddedEvent;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.handler.data.PlacedPrediction;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class DatabaseHandlerTest{
	private static final String CHANNEL_ID = "channel-id";
	private static final String CHANNEL_NAME = "channel-name";
	private static final String EVENT_ID = "event-id";
	private static final String EVENT_TITLE = "event-title";
	private static final Instant NOW = Instant.parse("2020-02-18T12:47:52.000Z");
	private static final int BALANCE = 12324;
    
    private final static String BADGE_1 = "blue-1";
    private final static String BADGE_2 = "pink-2";
    private final static String USERNAME = "username";
    
	@InjectMocks
	private DatabaseHandler tested;
	
	@Mock
	private IDatabase database;
    @Mock
    private Event eventData;
    @Mock
    private Outcome blueOutcome;
    @Mock
    private Outcome pinkOutcome;
    @Mock
    private Badge blueBadge;
    @Mock
    private Badge pinkBadge;
    @Mock
    private Predictor predictor;
    
    @BeforeEach
    void setUp(){
        lenient().when(eventData.getOutcomes()).thenReturn(List.of(blueOutcome, pinkOutcome));
        lenient().when(eventData.getTitle()).thenReturn(EVENT_TITLE);
        lenient().when(eventData.getChannelId()).thenReturn(CHANNEL_ID);
        lenient().when(eventData.getEndedAt()).thenReturn(TimeFactory.nowZoned());
        lenient().when(eventData.getCreatedAt()).thenReturn(TimeFactory.nowZoned());
        lenient().when(eventData.getId()).thenReturn(EVENT_ID);
        lenient().when(eventData.getWinningOutcomeId()).thenReturn("blue-id");
        
        lenient().when(blueOutcome.getTitle()).thenReturn("blue");
        lenient().when(blueOutcome.getId()).thenReturn("blue-id");
        lenient().when(blueOutcome.getTotalPoints()).thenReturn(1L);
        lenient().when(pinkOutcome.getTitle()).thenReturn("pink");
        lenient().when(pinkOutcome.getId()).thenReturn("pink-id");
        lenient().when(pinkOutcome.getTotalPoints()).thenReturn(2L);
        
        lenient().when(blueOutcome.getBadge()).thenReturn(blueBadge);
        lenient().when(pinkOutcome.getBadge()).thenReturn(pinkBadge);
        lenient().when(blueBadge.getVersion()).thenReturn(BADGE_1);
        lenient().when(pinkBadge.getVersion()).thenReturn(BADGE_2);
        
        lenient().when(predictor.getUserDisplayName()).thenReturn(USERNAME);
        lenient().when(blueOutcome.getTopPredictors()).thenReturn(List.of(predictor, predictor));
        lenient().when(pinkOutcome.getTopPredictors()).thenReturn(List.of(predictor, predictor));
    }
    
	@Test
	void onStreamerAdded() throws SQLException{
		var event = mock(StreamerAddedEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getStreamerUsername()).thenReturn(Optional.of(CHANNEL_NAME));
		
		tested.onEvent(event);
		
		verify(database).createChannel(CHANNEL_ID, CHANNEL_NAME);
	}
	
	@Test
	void onStreamerAddedDbException() throws SQLException{
		var event = mock(StreamerAddedEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getStreamerUsername()).thenReturn(Optional.of(CHANNEL_NAME));
		
		doThrow(new SQLException("For tests")).when(database).createChannel(anyString(), anyString());
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database).createChannel(CHANNEL_ID, CHANNEL_NAME);
	}
	
	@Test
	void onStreamerAddedUnknownUsername() throws SQLException{
		var event = mock(StreamerAddedEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getStreamerUsername()).thenReturn(Optional.empty());
		
		tested.onEvent(event);
		
		verify(database, never()).createChannel(anyString(), anyString());
	}
	
	@Test
	void onStreamUp() throws SQLException{
		var event = mock(StreamUpEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		
		tested.onEvent(event);
		
		verify(database).updateChannelStatusTime(CHANNEL_ID, NOW);
	}
	
	@Test
	void onStreamUpDatabaseException() throws SQLException{
		var event = mock(StreamUpEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		
		doThrow(new SQLException("For tests")).when(database).updateChannelStatusTime(anyString(), any());
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database).updateChannelStatusTime(CHANNEL_ID, NOW);
	}
	
	@Test
	void onStreamDown() throws SQLException{
		var event = mock(StreamDownEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		
		tested.onEvent(event);
		
		verify(database).updateChannelStatusTime(CHANNEL_ID, NOW);
	}
	
	@Test
	void onStreamDownDatabaseException() throws SQLException{
		var event = mock(StreamDownEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		
		doThrow(new SQLException("For tests")).when(database).updateChannelStatusTime(anyString(), any());
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database).updateChannelStatusTime(CHANNEL_ID, NOW);
	}
	
	@Test
	void onPointsEarned() throws SQLException{
		var balance = mock(Balance.class);
		when(balance.getBalance()).thenReturn(BALANCE);
		
		var pointGain = mock(PointGain.class);
		when(pointGain.getReasonCode()).thenReturn(PointReasonCode.RAID);
		
		var pointsEarnedData = mock(PointsEarnedData.class);
		when(pointsEarnedData.getBalance()).thenReturn(balance);
		when(pointsEarnedData.getPointGain()).thenReturn(pointGain);
		
		var event = mock(PointsEarnedEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		when(event.getPointsEarnedData()).thenReturn(pointsEarnedData);
		
		tested.onEvent(event);
		
		verify(database).addBalance(CHANNEL_ID, BALANCE, "RAID", NOW);
	}
	
	@Test
	void onPointsEarnedDatabaseException() throws SQLException{
		var balance = mock(Balance.class);
		when(balance.getBalance()).thenReturn(BALANCE);
		
		var pointGain = mock(PointGain.class);
		when(pointGain.getReasonCode()).thenReturn(PointReasonCode.RAID);
		
		var pointsEarnedData = mock(PointsEarnedData.class);
		when(pointsEarnedData.getBalance()).thenReturn(balance);
		when(pointsEarnedData.getPointGain()).thenReturn(pointGain);
		
		var event = mock(PointsEarnedEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		when(event.getPointsEarnedData()).thenReturn(pointsEarnedData);
		
		doThrow(new SQLException("For tests")).when(database).addBalance(anyString(), anyInt(), anyString(), any());
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database).addBalance(CHANNEL_ID, BALANCE, "RAID", NOW);
	}
	
	@Test
	void onPointsSpent() throws SQLException{
		var balance = mock(Balance.class);
		when(balance.getBalance()).thenReturn(BALANCE);
		
		var pointsSpentData = mock(PointsSpentData.class);
		when(pointsSpentData.getBalance()).thenReturn(balance);
		
		var event = mock(PointsSpentEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		when(event.getPointsSpentData()).thenReturn(pointsSpentData);
		
		tested.onEvent(event);
		
		verify(database).addBalance(CHANNEL_ID, BALANCE, null, NOW);
	}
	
	@Test
	void onPointsSpentDatabaseException() throws SQLException{
		var balance = mock(Balance.class);
		when(balance.getBalance()).thenReturn(BALANCE);
		
		var pointsSpentData = mock(PointsSpentData.class);
		when(pointsSpentData.getBalance()).thenReturn(balance);
		
		var event = mock(PointsSpentEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		when(event.getPointsSpentData()).thenReturn(pointsSpentData);
		
		doThrow(new SQLException("For tests")).when(database).addBalance(anyString(), anyInt(), anyString(), any());
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database).addBalance(CHANNEL_ID, BALANCE, null, NOW);
	}
	
	@Test
	void onPredictionMade() throws SQLException{
		var placedPrediction = mock(PlacedPrediction.class);
		when(placedPrediction.getAmount()).thenReturn(123);
		when(placedPrediction.getEventId()).thenReturn(EVENT_ID);
		
		var event = mock(PredictionMadeEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		when(event.getPlacedPrediction()).thenReturn(placedPrediction);
		
		tested.onEvent(event);
		
		verify(database).addPrediction(CHANNEL_ID, EVENT_ID, "PREDICTED", "123", NOW);
	}
	
	@Test
	void onPredictionMadeDatabaseException() throws SQLException{
		var placedPrediction = mock(PlacedPrediction.class);
		when(placedPrediction.getAmount()).thenReturn(123);
		when(placedPrediction.getEventId()).thenReturn(EVENT_ID);
		
		var event = mock(PredictionMadeEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		when(event.getPlacedPrediction()).thenReturn(placedPrediction);
		
		doThrow(new SQLException("For tests")).when(database).addPrediction(anyString(), anyString(), anyString(), anyString(), any());
		
		tested.onEvent(event);
		
		verify(database).addPrediction(CHANNEL_ID, EVENT_ID, "PREDICTED", "123", NOW);
	}
	
	@Test
	void onPredictionResult() throws SQLException{
		var gain = "GAIN text";
		
		var prediction = mock(Prediction.class);
		when(prediction.getEventId()).thenReturn(EVENT_ID);
		
		var predictionResultData = mock(PredictionResultData.class);
		when(predictionResultData.getPrediction()).thenReturn(prediction);
		
		var event = mock(PredictionResultEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		when(event.getGain()).thenReturn(gain);
		when(event.getPredictionResultData()).thenReturn(predictionResultData);
		
		tested.onEvent(event);
		
		verify(database).addPrediction(CHANNEL_ID, EVENT_ID, "RESULT", gain, NOW);
	}
	
	@Test
	void onPredictionResultDatabaseException() throws SQLException{
		var gain = "GAIN text";
		
		var prediction = mock(Prediction.class);
		when(prediction.getEventId()).thenReturn(EVENT_ID);
		
		var predictionResultData = mock(PredictionResultData.class);
		when(predictionResultData.getPrediction()).thenReturn(prediction);
		
		var event = mock(PredictionResultEvent.class);
		when(event.getStreamerId()).thenReturn(CHANNEL_ID);
		when(event.getInstant()).thenReturn(NOW);
		when(event.getGain()).thenReturn(gain);
		when(event.getPredictionResultData()).thenReturn(predictionResultData);
		
		doThrow(new SQLException("For tests")).when(database).addPrediction(anyString(), anyString(), anyString(), anyString(), any());
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database).addPrediction(CHANNEL_ID, EVENT_ID, "RESULT", gain, NOW);
	}
	
	@Test
	void closeClosesDatabase(){
		tested.close();
		
		verify(database).close();
	}
    
    @Test
    void onActivePredictionUpdate() throws SQLException{
        var event = mock(EventUpdatedEvent.class);
        
        when(event.getEvent()).thenReturn(eventData);
        when(event.getStreamerUsername()).thenReturn(Optional.of(CHANNEL_NAME));
    
        when(eventData.getStatus()).thenReturn(EventStatus.ACTIVE);
    
        assertDoesNotThrow(() -> tested.onEvent(event));
        
        verify(database, times(2)).addUserPrediction(USERNAME, CHANNEL_NAME, BADGE_1);
        verify(database, times(2)).addUserPrediction(USERNAME, CHANNEL_NAME, BADGE_2);
    }
    
    @Test
    void onCancelledPredictionUpdate() throws SQLException{
        var event = mock(EventUpdatedEvent.class);
        
        when(event.getEvent()).thenReturn(eventData);
        when(event.getStreamerUsername()).thenReturn(Optional.of(CHANNEL_NAME));
        
        when(eventData.getStatus()).thenReturn(EventStatus.CANCELED);
    
        assertDoesNotThrow(() -> tested.onEvent(event));
    
        assert eventData.getEndedAt() != null;
        verify(database).cancelPrediction(EVENT_ID, CHANNEL_ID, EVENT_TITLE, eventData.getCreatedAt().toInstant(), eventData.getEndedAt().toInstant());
    }
    
    @Test
    void onResolvedPredictionUpdate() throws SQLException{
        var event = mock(EventUpdatedEvent.class);
        
        when(event.getEvent()).thenReturn(eventData);
        when(event.getStreamerUsername()).thenReturn(Optional.of(CHANNEL_NAME));
        
        when(eventData.getStatus()).thenReturn(EventStatus.RESOLVED);
    
        assertDoesNotThrow(() -> tested.onEvent(event));
    
        double totalPoints = eventData.getOutcomes().stream().mapToDouble(Outcome::getTotalPoints).sum();
        double returnRatio = (blueOutcome.getTotalPoints() / totalPoints) + 1;
    
        assert eventData.getEndedAt() != null;
        verify(database).resolvePrediction(EVENT_ID, CHANNEL_ID, EVENT_TITLE, eventData.getCreatedAt().toInstant(), eventData.getEndedAt().toInstant(),
                blueOutcome.getTitle(), blueOutcome.getBadge().getVersion(), returnRatio);
    }
}