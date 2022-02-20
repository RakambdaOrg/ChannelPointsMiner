package fr.raksrinana.channelpointsminer.database;

import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.Balance;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.pointsspent.PointsSpentData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionresult.PredictionResultData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PointGain;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PointReasonCode;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Prediction;
import fr.raksrinana.channelpointsminer.event.impl.PointsEarnedEvent;
import fr.raksrinana.channelpointsminer.event.impl.PointsSpentEvent;
import fr.raksrinana.channelpointsminer.event.impl.PredictionMadeEvent;
import fr.raksrinana.channelpointsminer.event.impl.PredictionResultEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamDownEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamUpEvent;
import fr.raksrinana.channelpointsminer.event.impl.StreamerAddedEvent;
import fr.raksrinana.channelpointsminer.handler.data.PlacedPrediction;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseHandlerTest{
	private static final String CHANNEL_ID = "channel-id";
	private static final String CHANNEL_NAME = "channel-name";
	private static final String EVENT_ID = "event-id";
	private static final Instant NOW = Instant.parse("2020-02-18T12:47:52.000Z");
	private static final int BALANCE = 12324;
	
	@InjectMocks
	private DatabaseHandler tested;
	
	@Mock
	private IDatabase database;
	
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
}