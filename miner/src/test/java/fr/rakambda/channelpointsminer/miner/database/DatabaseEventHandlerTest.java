package fr.rakambda.channelpointsminer.miner.database;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.pointsearned.Balance;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.pointsearned.PointsEarnedData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.pointsspent.PointsSpentData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.predictionresult.PredictionResultData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Badge;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.EventStatus;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.PointGain;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Prediction;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Predictor;
import fr.rakambda.channelpointsminer.miner.event.impl.ChatMessageEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.EventUpdatedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsEarnedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PointsSpentEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionMadeEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionResultEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamDownEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamUpEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerAddedEvent;
import fr.rakambda.channelpointsminer.miner.handler.data.PlacedPrediction;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class DatabaseEventHandlerTest{
	private static final String CHANNEL_ID = "channel-id";
	private static final String CHANNEL_NAME = "channel-name";
	private static final String EVENT_ID = "event-id";
	private static final Instant NOW = Instant.parse("2020-02-18T12:47:52.000Z");
	private static final int BALANCE = 12324;
	private static final String BADGE_1 = "blue-1";
	private static final String BADGE_2 = "pink-2";
	private static final String USERNAME1 = "username1";
	private static final String USERNAME2 = "username2";
	private static final String USERNAME3 = "username3";
	private static final String USERNAME4 = "username4";
	private static final long BLUE_POINTS = 1L;
	private static final long PINK_POINTS = 2L;
	private static final String BLUE_TITLE = "blue";
	private static final String PINK_TITLE = "pink";
	private final static String ACTOR = "username";
	private final static String PREDICTION = "color";
	private final static String BADGE_PREDICTION_INFO = "badges=predictions/color,sub";
	private final static String BADGE_NO_PREDICTION_INFO = "badges=sub";
	
	private DatabaseEventHandler tested;
	
	@Mock
	private IDatabase database;
	@Mock
	private Event eventData;
	@Mock
	private Outcome blueOutcome;
	@Mock
	private Outcome pinkOutcome;
	
	@BeforeEach
	void setUp(){
		lenient().when(eventData.getOutcomes()).thenReturn(List.of(blueOutcome, pinkOutcome));
		lenient().when(eventData.getChannelId()).thenReturn(CHANNEL_ID);
		lenient().when(eventData.getId()).thenReturn(EVENT_ID);
		lenient().when(eventData.getWinningOutcomeId()).thenReturn("blue-id");
		
		lenient().when(blueOutcome.getTitle()).thenReturn(BLUE_TITLE);
		lenient().when(blueOutcome.getId()).thenReturn("blue-id");
		lenient().when(blueOutcome.getTotalPoints()).thenReturn(BLUE_POINTS);
		lenient().when(pinkOutcome.getTitle()).thenReturn(PINK_TITLE);
		lenient().when(pinkOutcome.getId()).thenReturn("pink-id");
		lenient().when(pinkOutcome.getTotalPoints()).thenReturn(PINK_POINTS);
		
		var blueBadge = mock(Badge.class);
		var pinkBadge = mock(Badge.class);
		
		lenient().when(blueOutcome.getBadge()).thenReturn(blueBadge);
		lenient().when(pinkOutcome.getBadge()).thenReturn(pinkBadge);
		lenient().when(blueBadge.getVersion()).thenReturn(BADGE_1);
		lenient().when(pinkBadge.getVersion()).thenReturn(BADGE_2);
		
		var predictor1 = mock(Predictor.class);
		var predictor2 = mock(Predictor.class);
		var predictor3 = mock(Predictor.class);
		var predictor4 = mock(Predictor.class);
		
		lenient().when(predictor1.getUserDisplayName()).thenReturn(USERNAME1);
		lenient().when(predictor2.getUserDisplayName()).thenReturn(USERNAME2);
		lenient().when(predictor3.getUserDisplayName()).thenReturn(USERNAME3);
		lenient().when(predictor4.getUserDisplayName()).thenReturn(USERNAME4);
		lenient().when(blueOutcome.getTopPredictors()).thenReturn(List.of(predictor1, predictor2));
		lenient().when(pinkOutcome.getTopPredictors()).thenReturn(List.of(predictor3, predictor4));
		
		tested = new DatabaseEventHandler(database, true);
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
		when(pointGain.getReasonCode()).thenReturn("RAID");
		
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
		when(pointGain.getReasonCode()).thenReturn("RAID");
		
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
		//TODO test database.addUserPrediction
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
	void closeClosesDatabase() throws IOException{
		tested.close();
		
		verify(database).close();
	}
	
	@Test
	void onActivePredictionUpdate() throws SQLException{
		var event = mock(EventUpdatedEvent.class);
		
		when(event.getEvent()).thenReturn(eventData);
		when(eventData.getChannelId()).thenReturn(CHANNEL_ID);
		
		when(eventData.getStatus()).thenReturn(EventStatus.ACTIVE);
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database).addUserPrediction(USERNAME1, CHANNEL_ID, BADGE_1);
		verify(database).addUserPrediction(USERNAME2, CHANNEL_ID, BADGE_1);
		verify(database).addUserPrediction(USERNAME3, CHANNEL_ID, BADGE_2);
		verify(database).addUserPrediction(USERNAME4, CHANNEL_ID, BADGE_2);
	}
	
	@Test
	void onActivePredictionUpdateWithoutUserPredictions() throws SQLException{
		tested = new DatabaseEventHandler(database, false);
		
		var event = mock(EventUpdatedEvent.class);
		
		when(event.getEvent()).thenReturn(eventData);
		
		when(eventData.getStatus()).thenReturn(EventStatus.ACTIVE);
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database, never()).addUserPrediction(anyString(), anyString(), anyString());
	}
	
	@Test
	void onCancelledPredictionUpdate() throws SQLException{
		var event = mock(EventUpdatedEvent.class);
		
		when(event.getEvent()).thenReturn(eventData);
		when(event.getStreamerUsername()).thenReturn(CHANNEL_NAME);
		
		when(eventData.getStatus()).thenReturn(EventStatus.CANCELED);
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database).cancelPrediction(eventData);
	}
	
	@Test
	void onResolvedPredictionUpdate() throws SQLException{
		var event = mock(EventUpdatedEvent.class);
		
		when(event.getEvent()).thenReturn(eventData);
		when(event.getStreamerUsername()).thenReturn(CHANNEL_NAME);
		
		when(eventData.getStatus()).thenReturn(EventStatus.RESOLVED);
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		var returnRatio = (double) (BLUE_POINTS + PINK_POINTS) / BLUE_POINTS;
		
		verify(database).resolvePrediction(eventData, BLUE_TITLE, BADGE_1, returnRatio);
	}
	
	@Test
	void onResolvedPredictionUpdateWithNoBetsOnWinningSide() throws SQLException{
		var event = mock(EventUpdatedEvent.class);
		
		when(event.getEvent()).thenReturn(eventData);
		when(event.getStreamerUsername()).thenReturn(CHANNEL_NAME);
		
		when(blueOutcome.getTotalPoints()).thenReturn(0L);
		
		when(eventData.getStatus()).thenReturn(EventStatus.RESOLVED);
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database).resolvePrediction(eventData, BLUE_TITLE, BADGE_1, 100000);
	}
	
	@Test
	void onChatMessagePredictionRecorded() throws SQLException{
		var event = mock(ChatMessageEvent.class);
		
		when(event.getStreamer()).thenReturn(CHANNEL_NAME);
		when(event.getActor()).thenReturn(ACTOR);
		when(event.getBadges()).thenReturn(BADGE_PREDICTION_INFO);
		
		when(database.getStreamerIdFromName(CHANNEL_NAME)).thenReturn(Optional.of(CHANNEL_ID));
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database).addUserPrediction(ACTOR, CHANNEL_ID, PREDICTION);
	}
	
	@Test
	void onChatMessagePredictionChannelUnknown() throws SQLException{
		var event = mock(ChatMessageEvent.class);
		
		when(event.getStreamer()).thenReturn(CHANNEL_NAME);
		when(event.getActor()).thenReturn(ACTOR);
		when(event.getBadges()).thenReturn(BADGE_PREDICTION_INFO);
		
		when(database.getStreamerIdFromName(CHANNEL_NAME)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database, never()).addUserPrediction(ACTOR, CHANNEL_ID, PREDICTION);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"",
			BADGE_NO_PREDICTION_INFO
	})
	void onChatMessageNoPredictionRecorded(String badges) throws SQLException{
		var event = mock(ChatMessageEvent.class);
		
		when(event.getBadges()).thenReturn(badges);
		
		assertDoesNotThrow(() -> tested.onEvent(event));
		
		verify(database, never()).addUserPrediction(any(), any(), any());
	}
}