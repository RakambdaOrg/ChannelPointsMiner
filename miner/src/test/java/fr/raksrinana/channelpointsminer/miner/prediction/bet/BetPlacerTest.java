package fr.raksrinana.channelpointsminer.miner.prediction.bet;

import fr.raksrinana.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.makeprediction.MakePredictionData;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types.MakePredictionError;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types.MakePredictionErrorCode;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types.MakePredictionPayload;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.EventStatus;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.handler.data.PredictionState;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.action.IPredictionAction;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.amount.IAmountCalculator;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.exception.NotEnoughUsersBetPlacementException;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.outcome.IOutcomePicker;
import fr.raksrinana.channelpointsminer.miner.streamer.PredictionSettings;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import fr.raksrinana.channelpointsminer.miner.streamer.StreamerSettings;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import fr.raksrinana.channelpointsminer.miner.util.CommonUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class BetPlacerTest{
	private static final int AMOUNT = 50;
	private static final String EVENT_ID = "event-id";
	private static final String OUTCOME_ID = "outcome-id";
	private static final String TRANSACTION_ID = "transaction-id";
	
	@InjectMocks
	private BetPlacer tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private IDatabase database;
	@Mock
	private BettingPrediction bettingPrediction;
	@Mock
	private Event event;
	@Mock
	private Streamer streamer;
	@Mock
	private StreamerSettings streamerSettings;
	@Mock
	private PredictionSettings predictionSettings;
	@Mock
	private IOutcomePicker outcomePicker;
	@Mock
	private IAmountCalculator amountCalculator;
	@Mock
	private Outcome outcome;
	@Mock
	private GQLResponse<MakePredictionData> gqlResponse;
	@Mock
	private MakePredictionData makePredictionData;
	@Mock
	private MakePredictionPayload makePrediction;
	@Mock
	private IPredictionAction predictionAction;
	
	@BeforeEach
	void setUp() throws BetPlacementException{
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getDatabase()).thenReturn(database);
		lenient().when(bettingPrediction.getEvent()).thenReturn(event);
		lenient().when(bettingPrediction.getStreamer()).thenReturn(streamer);
		
		lenient().when(streamer.getSettings()).thenReturn(streamerSettings);
		lenient().when(streamerSettings.getPredictions()).thenReturn(predictionSettings);
		lenient().when(predictionSettings.getOutcomePicker()).thenReturn(outcomePicker);
		lenient().when(predictionSettings.getAmountCalculator()).thenReturn(amountCalculator);
		lenient().when(predictionSettings.getActions()).thenReturn(List.of());
		
		lenient().when(event.getId()).thenReturn(EVENT_ID);
		lenient().when(event.getStatus()).thenReturn(EventStatus.ACTIVE);
		
		lenient().when(outcome.getId()).thenReturn(OUTCOME_ID);
		
		lenient().when(outcomePicker.chooseOutcome(bettingPrediction, database)).thenReturn(outcome);
		lenient().when(amountCalculator.calculateAmount(bettingPrediction, outcome)).thenReturn(AMOUNT);
		
		lenient().when(gqlApi.makePrediction(EVENT_ID, OUTCOME_ID, AMOUNT, TRANSACTION_ID)).thenReturn(Optional.of(gqlResponse));
		lenient().when(gqlResponse.getData()).thenReturn(makePredictionData);
		lenient().when(makePredictionData.getMakePrediction()).thenReturn(makePrediction);
	}
	
	@Test
	void eventNotActive(){
		when(event.getStatus()).thenReturn(EventStatus.LOCKED);
		
		assertDoesNotThrow(() -> tested.placeBet(bettingPrediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
		verify(bettingPrediction).setState(PredictionState.BET_ERROR);
	}
	
	@Test
	void outcomeException() throws BetPlacementException{
		when(outcomePicker.chooseOutcome(bettingPrediction, database)).thenThrow(new BetPlacementException("For tests"));
		
		assertDoesNotThrow(() -> tested.placeBet(bettingPrediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
		verify(bettingPrediction).setState(PredictionState.BET_ERROR);
	}
	
	@Test
	void outcomeException2() throws BetPlacementException{
		when(outcomePicker.chooseOutcome(bettingPrediction, database)).thenThrow(new NotEnoughUsersBetPlacementException(0));
		
		assertDoesNotThrow(() -> tested.placeBet(bettingPrediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
		verify(bettingPrediction).setState(PredictionState.BET_ERROR);
	}
	
	@Test
	void amountException() throws BetPlacementException{
		when(amountCalculator.calculateAmount(bettingPrediction, outcome)).thenThrow(new BetPlacementException("For tests"));
		
		assertDoesNotThrow(() -> tested.placeBet(bettingPrediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
		verify(bettingPrediction).setState(PredictionState.BET_ERROR);
	}
	
	@ParameterizedTest
	@ValueSource(ints = {
			5,
			6,
			7,
			8,
			9
	})
	void amountTooLow() throws BetPlacementException{
		when(amountCalculator.calculateAmount(bettingPrediction, outcome)).thenReturn(5);
		
		assertDoesNotThrow(() -> tested.placeBet(bettingPrediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
		verify(bettingPrediction).setState(PredictionState.BET_ERROR);
	}
	
	@Test
	void nominalOnLimit() throws BetPlacementException{
		var amount = 10;
		
		try(var transactionIdFactory = mockStatic(CommonUtils.class)){
			transactionIdFactory.when(() -> CommonUtils.randomHex(32)).thenReturn(TRANSACTION_ID);
			when(amountCalculator.calculateAmount(bettingPrediction, outcome)).thenReturn(amount);
			when(gqlApi.makePrediction(EVENT_ID, OUTCOME_ID, amount, TRANSACTION_ID)).thenReturn(Optional.of(gqlResponse));
			
			assertDoesNotThrow(() -> tested.placeBet(bettingPrediction));
			
			verify(gqlApi).makePrediction(EVENT_ID, OUTCOME_ID, amount, TRANSACTION_ID);
			verify(bettingPrediction, never()).setState(any());
		}
	}
	
	@Test
	void nominal(){
		try(var transactionIdFactory = mockStatic(CommonUtils.class)){
			transactionIdFactory.when(() -> CommonUtils.randomHex(32)).thenReturn(TRANSACTION_ID);
			
			assertDoesNotThrow(() -> tested.placeBet(bettingPrediction));
			
			verify(gqlApi).makePrediction(EVENT_ID, OUTCOME_ID, AMOUNT, TRANSACTION_ID);
			verify(bettingPrediction, never()).setState(any());
		}
	}
	
	@Test
	void nominalWithActionModification() throws BetPlacementException{
		var newAmount = AMOUNT + 10;
		try(var transactionIdFactory = mockStatic(CommonUtils.class)){
			transactionIdFactory.when(() -> CommonUtils.randomHex(32)).thenReturn(TRANSACTION_ID);
			
			when(predictionSettings.getActions()).thenReturn(List.of(predictionAction));
			doAnswer(invocation -> {
				var placement = invocation.getArgument(0, Placement.class);
				placement.setAmount(newAmount);
				return null;
			}).when(predictionAction).perform(any());
			when(gqlApi.makePrediction(EVENT_ID, OUTCOME_ID, newAmount, TRANSACTION_ID)).thenReturn(Optional.of(gqlResponse));
			
			assertDoesNotThrow(() -> tested.placeBet(bettingPrediction));
			
			verify(gqlApi).makePrediction(EVENT_ID, OUTCOME_ID, newAmount, TRANSACTION_ID);
			verify(bettingPrediction, never()).setState(any());
			verify(predictionAction).perform(Placement.builder()
					.bettingPrediction(bettingPrediction)
					.outcome(outcome)
					.amount(newAmount)
					.build());
		}
	}
	
	@Test
	void nominalWithActionThrowing() throws BetPlacementException{
		try(var transactionIdFactory = mockStatic(CommonUtils.class)){
			transactionIdFactory.when(() -> CommonUtils.randomHex(32)).thenReturn(TRANSACTION_ID);
			
			when(predictionSettings.getActions()).thenReturn(List.of(predictionAction));
			doThrow(new BetPlacementException("For tests")).when(predictionAction).perform(any());
			
			assertDoesNotThrow(() -> tested.placeBet(bettingPrediction));
			
			verify(gqlApi, never()).makePrediction(anyString(), anyString(), anyInt(), anyString());
			verify(bettingPrediction).setState(PredictionState.BET_ERROR);
			verify(predictionAction).perform(Placement.builder()
					.bettingPrediction(bettingPrediction)
					.outcome(outcome)
					.amount(AMOUNT)
					.build());
		}
	}
	
	@Test
	void placeNoData(){
		try(var transactionIdFactory = mockStatic(CommonUtils.class)){
			transactionIdFactory.when(() -> CommonUtils.randomHex(32)).thenReturn(TRANSACTION_ID);
			
			when(gqlApi.makePrediction(EVENT_ID, OUTCOME_ID, AMOUNT, TRANSACTION_ID)).thenReturn(Optional.empty());
			
			assertDoesNotThrow(() -> tested.placeBet(bettingPrediction));
			
			verify(gqlApi).makePrediction(EVENT_ID, OUTCOME_ID, AMOUNT, TRANSACTION_ID);
			verify(bettingPrediction).setState(PredictionState.BET_ERROR);
		}
	}
	
	@Test
	void placeError(){
		try(var transactionIdFactory = mockStatic(CommonUtils.class)){
			transactionIdFactory.when(() -> CommonUtils.randomHex(32)).thenReturn(TRANSACTION_ID);
			
			when(makePrediction.getError()).thenReturn(MakePredictionError.builder()
					.code(MakePredictionErrorCode.NOT_ENOUGH_POINTS)
					.build());
			
			assertDoesNotThrow(() -> tested.placeBet(bettingPrediction));
			
			verify(gqlApi).makePrediction(EVENT_ID, OUTCOME_ID, AMOUNT, TRANSACTION_ID);
			verify(bettingPrediction).setState(PredictionState.BET_ERROR);
		}
	}
}