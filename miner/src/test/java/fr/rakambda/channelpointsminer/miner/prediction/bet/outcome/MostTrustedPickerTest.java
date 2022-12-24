package fr.rakambda.channelpointsminer.miner.prediction.bet.outcome;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Badge;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.database.IDatabase;
import fr.rakambda.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import fr.rakambda.channelpointsminer.miner.factory.DatabaseFactory;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@SuppressWarnings("ResultOfMethodCallIgnored")
@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class MostTrustedPickerTest{
    
    private final static int MIN_TOTAL_BETS_PLACED_BY_USER = 5;
	private final static int MIN_TOTAL_BETS_PLACED_ON_PREDICTION = 10;
	private final static int MIN_TOTAL_BETS_PLACED_ON_OUTCOME = 5;
	private final static String BADGE_1 = "blue-1";
	private final static String BADGE_2 = "pink-2";
	
	private final static String CHANNEL_ID = "channelid";
	
	private final MostTrustedPicker tested = MostTrustedPicker.builder()
			.minTotalBetsPlacedByUser(MIN_TOTAL_BETS_PLACED_BY_USER)
			.minTotalBetsPlacedOnPrediction(MIN_TOTAL_BETS_PLACED_ON_PREDICTION)
			.minTotalBetsPlacedOnOutcome(MIN_TOTAL_BETS_PLACED_ON_OUTCOME)
			.build();
	
	@Mock
	private IDatabase database;
	@Mock
	private BettingPrediction bettingPrediction;
	@Mock
	private Event event;
	@Mock
	private Outcome blueOutcome;
	@Mock
	private Outcome pinkOutcome;
	@Mock
	private Badge blueBadge;
	@Mock
    private Badge pinkBadge;
    @Mock
    private OutcomeStatistic outcomeStatisticBlue;
    @Mock
    private OutcomeStatistic outcomeStatisticPink;
    
    @BeforeEach
    void setUp() throws SQLException{
        lenient().when(bettingPrediction.getEvent()).thenReturn(event);
        lenient().when(event.getOutcomes()).thenReturn(List.of(blueOutcome, pinkOutcome));
        lenient().when(event.getTitle()).thenReturn("title");
        lenient().when(event.getChannelId()).thenReturn(CHANNEL_ID);
    
        lenient().when(blueOutcome.getTitle()).thenReturn("blue");
        lenient().when(pinkOutcome.getTitle()).thenReturn("pink");
        
        lenient().when(blueOutcome.getBadge()).thenReturn(blueBadge);
        lenient().when(pinkOutcome.getBadge()).thenReturn(pinkBadge);
        lenient().when(blueBadge.getVersion()).thenReturn(BADGE_1);
        lenient().when(pinkBadge.getVersion()).thenReturn(BADGE_2);
        
        lenient().when(outcomeStatisticBlue.getBadge()).thenReturn(BADGE_1);
        lenient().when(outcomeStatisticPink.getBadge()).thenReturn(BADGE_2);
    
        lenient().when(outcomeStatisticBlue.getAverageReturnOnInvestment()).thenReturn(1.1);
        lenient().when(outcomeStatisticPink.getAverageReturnOnInvestment()).thenReturn(1.0);
    
        lenient().when(outcomeStatisticBlue.getUserCnt()).thenReturn(10);
        lenient().when(outcomeStatisticPink.getUserCnt()).thenReturn(10);
        
        lenient().when(database.getOutcomeStatisticsForChannel(CHANNEL_ID, MIN_TOTAL_BETS_PLACED_BY_USER)).thenReturn(List.of(outcomeStatisticBlue, outcomeStatisticPink));
    }
    
    @Test
    void chooseOutcome(){
        try(var databaseFactory = mockStatic(DatabaseFactory.class)){
	        Outcome outcome = Assertions.assertDoesNotThrow(() -> tested.chooseOutcome(bettingPrediction, database));
            
            assertEquals(BADGE_1, outcome.getBadge().getVersion());
        }
    }
    
    @Test
    void notEnoughTotalBetsPlaced(){
        try(var databaseFactory = mockStatic(DatabaseFactory.class)){
            lenient().when(outcomeStatisticBlue.getUserCnt()).thenReturn(6);
            lenient().when(outcomeStatisticPink.getUserCnt()).thenReturn(2);
	
	        assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(bettingPrediction, database));
        }
    }
    
    @Test
    void notEnoughBetsPlacedOnOutcome(){
        try(var databaseFactory = mockStatic(DatabaseFactory.class)){
            lenient().when(outcomeStatisticBlue.getUserCnt()).thenReturn(2);
            lenient().when(outcomeStatisticPink.getUserCnt()).thenReturn(20);
	
	        assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(bettingPrediction, database));
        }
    }
    
    @Test
    void databaseThrowsException() throws SQLException{
        try(var databaseFactory = mockStatic(DatabaseFactory.class)){
            when(database.getOutcomeStatisticsForChannel(CHANNEL_ID, MIN_TOTAL_BETS_PLACED_BY_USER)).thenThrow(new SQLException(""));
	
	        assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(bettingPrediction, database));
        }
    }
    
    @Test
    void emptyStatistics() throws SQLException{
        try(var databaseFactory = mockStatic(DatabaseFactory.class)){
            when(database.getOutcomeStatisticsForChannel(CHANNEL_ID, MIN_TOTAL_BETS_PLACED_BY_USER)).thenReturn(Collections.emptyList());
	
	        assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(bettingPrediction, database));
        }
    }
}