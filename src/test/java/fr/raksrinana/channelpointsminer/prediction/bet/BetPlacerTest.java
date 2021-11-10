package fr.raksrinana.channelpointsminer.prediction.bet;

import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.makeprediction.MakePredictionData;
import fr.raksrinana.channelpointsminer.api.gql.data.types.MakePredictionError;
import fr.raksrinana.channelpointsminer.api.gql.data.types.MakePredictionErrorCode;
import fr.raksrinana.channelpointsminer.api.gql.data.types.MakePredictionPayload;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.EventStatus;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.factory.TransactionIdFactory;
import fr.raksrinana.channelpointsminer.handler.data.Prediction;
import fr.raksrinana.channelpointsminer.handler.data.PredictionState;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.prediction.bet.action.PredictionAction;
import fr.raksrinana.channelpointsminer.prediction.bet.amount.AmountCalculator;
import fr.raksrinana.channelpointsminer.prediction.bet.outcome.OutcomePicker;
import fr.raksrinana.channelpointsminer.streamer.PredictionSettings;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import fr.raksrinana.channelpointsminer.streamer.StreamerSettings;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

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
	private Prediction prediction;
	@Mock
	private Event event;
	@Mock
	private Streamer streamer;
	@Mock
	private StreamerSettings streamerSettings;
	@Mock
	private PredictionSettings predictionSettings;
	@Mock
	private OutcomePicker outcomePicker;
	@Mock
	private AmountCalculator amountCalculator;
	@Mock
	private Outcome outcome;
	@Mock
	private GQLResponse<MakePredictionData> gqlResponse;
	@Mock
	private MakePredictionData makePredictionData;
	@Mock
	private MakePredictionPayload makePrediction;
	@Mock
	private PredictionAction predictionAction;
	
	@BeforeEach
	void setUp() throws BetPlacementException{
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(prediction.getEvent()).thenReturn(event);
		lenient().when(prediction.getStreamer()).thenReturn(streamer);
		
		lenient().when(streamer.getSettings()).thenReturn(streamerSettings);
		lenient().when(streamerSettings.getPredictions()).thenReturn(predictionSettings);
		lenient().when(predictionSettings.getOutcomePicker()).thenReturn(outcomePicker);
		lenient().when(predictionSettings.getAmountCalculator()).thenReturn(amountCalculator);
		lenient().when(predictionSettings.getActions()).thenReturn(List.of());
		
		lenient().when(event.getId()).thenReturn(EVENT_ID);
		lenient().when(event.getStatus()).thenReturn(EventStatus.ACTIVE);
		
		lenient().when(outcome.getId()).thenReturn(OUTCOME_ID);
		
		lenient().when(outcomePicker.chooseOutcome(prediction)).thenReturn(outcome);
		lenient().when(amountCalculator.calculateAmount(prediction, outcome)).thenReturn(AMOUNT);
		
		lenient().when(gqlApi.makePrediction(EVENT_ID, OUTCOME_ID, AMOUNT, TRANSACTION_ID)).thenReturn(Optional.of(gqlResponse));
		lenient().when(gqlResponse.getData()).thenReturn(makePredictionData);
		lenient().when(makePredictionData.getMakePrediction()).thenReturn(makePrediction);
	}
	
	@Test
	void eventNotActive(){
		when(event.getStatus()).thenReturn(EventStatus.LOCKED);
		
		assertDoesNotThrow(() -> tested.placeBet(prediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
		verify(prediction).setState(PredictionState.BET_ERROR);
	}
	
	@Test
	void outcomeException() throws BetPlacementException{
		when(outcomePicker.chooseOutcome(prediction)).thenThrow(new BetPlacementException("For tests"));
		
		assertDoesNotThrow(() -> tested.placeBet(prediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
		verify(prediction).setState(PredictionState.BET_ERROR);
	}
	
	@Test
	void amountException() throws BetPlacementException{
		when(amountCalculator.calculateAmount(prediction, outcome)).thenThrow(new BetPlacementException("For tests"));
		
		assertDoesNotThrow(() -> tested.placeBet(prediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
		verify(prediction).setState(PredictionState.BET_ERROR);
	}
	
	@Test
	void amountTooLow() throws BetPlacementException{
		when(amountCalculator.calculateAmount(prediction, outcome)).thenReturn(5);
		
		assertDoesNotThrow(() -> tested.placeBet(prediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
		verify(prediction).setState(PredictionState.BET_ERROR);
	}
	
	@Test
	void nominal(){
		try(var transactionIdFactory = mockStatic(TransactionIdFactory.class)){
			transactionIdFactory.when(TransactionIdFactory::create).thenReturn(TRANSACTION_ID);
			
			assertDoesNotThrow(() -> tested.placeBet(prediction));
			
			verify(gqlApi).makePrediction(EVENT_ID, OUTCOME_ID, AMOUNT, TRANSACTION_ID);
			verify(prediction, never()).setState(any());
		}
	}
	
	@Test
	void nominalWithActionModification() throws BetPlacementException{
		var newAmount = AMOUNT + 10;
		try(var transactionIdFactory = mockStatic(TransactionIdFactory.class)){
			transactionIdFactory.when(TransactionIdFactory::create).thenReturn(TRANSACTION_ID);
			
			when(predictionSettings.getActions()).thenReturn(List.of(predictionAction));
			doAnswer(invocation -> {
				var placement = invocation.getArgument(0, Placement.class);
				placement.setAmount(newAmount);
				return null;
			}).when(predictionAction).perform(any());
			when(gqlApi.makePrediction(EVENT_ID, OUTCOME_ID, newAmount, TRANSACTION_ID)).thenReturn(Optional.of(gqlResponse));
			
			assertDoesNotThrow(() -> tested.placeBet(prediction));
			
			verify(gqlApi).makePrediction(EVENT_ID, OUTCOME_ID, newAmount, TRANSACTION_ID);
			verify(prediction, never()).setState(any());
			verify(predictionAction).perform(Placement.builder()
					.prediction(prediction)
					.outcome(outcome)
					.amount(newAmount)
					.build());
		}
	}
	
	@Test
	void nominalWithActionThrowing() throws BetPlacementException{
		try(var transactionIdFactory = mockStatic(TransactionIdFactory.class)){
			transactionIdFactory.when(TransactionIdFactory::create).thenReturn(TRANSACTION_ID);
			
			when(predictionSettings.getActions()).thenReturn(List.of(predictionAction));
			doThrow(new BetPlacementException("For tests")).when(predictionAction).perform(any());
			
			assertDoesNotThrow(() -> tested.placeBet(prediction));
			
			verify(gqlApi, never()).makePrediction(anyString(), anyString(), anyInt(), anyString());
			verify(prediction).setState(PredictionState.BET_ERROR);
			verify(predictionAction).perform(Placement.builder()
					.prediction(prediction)
					.outcome(outcome)
					.amount(AMOUNT)
					.build());
		}
	}
	
	@Test
	void placeNoData(){
		try(var transactionIdFactory = mockStatic(TransactionIdFactory.class)){
			transactionIdFactory.when(TransactionIdFactory::create).thenReturn(TRANSACTION_ID);
			
			when(gqlApi.makePrediction(EVENT_ID, OUTCOME_ID, AMOUNT, TRANSACTION_ID)).thenReturn(Optional.empty());
			
			assertDoesNotThrow(() -> tested.placeBet(prediction));
			
			verify(gqlApi).makePrediction(EVENT_ID, OUTCOME_ID, AMOUNT, TRANSACTION_ID);
			verify(prediction).setState(PredictionState.BET_ERROR);
		}
	}
	
	@Test
	void placeError(){
		try(var transactionIdFactory = mockStatic(TransactionIdFactory.class)){
			transactionIdFactory.when(TransactionIdFactory::create).thenReturn(TRANSACTION_ID);
			
			when(makePrediction.getError()).thenReturn(MakePredictionError.builder()
					.code(MakePredictionErrorCode.NOT_ENOUGH_POINTS)
					.build());
			
			assertDoesNotThrow(() -> tested.placeBet(prediction));
			
			verify(gqlApi).makePrediction(EVENT_ID, OUTCOME_ID, AMOUNT, TRANSACTION_ID);
			verify(prediction).setState(PredictionState.BET_ERROR);
		}
	}
}