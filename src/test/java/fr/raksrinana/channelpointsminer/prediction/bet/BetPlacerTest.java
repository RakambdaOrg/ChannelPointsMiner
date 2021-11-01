package fr.raksrinana.twitchminer.prediction.bet;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.EventStatus;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.twitchminer.factory.TransactionIdFactory;
import fr.raksrinana.twitchminer.handler.data.Prediction;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.prediction.bet.amount.AmountCalculator;
import fr.raksrinana.twitchminer.prediction.bet.outcome.OutcomePicker;
import fr.raksrinana.twitchminer.streamer.PredictionSettings;
import fr.raksrinana.twitchminer.streamer.Streamer;
import fr.raksrinana.twitchminer.streamer.StreamerSettings;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
	
	@BeforeEach
	void setUp() throws BetPlacementException{
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(prediction.getEvent()).thenReturn(event);
		lenient().when(prediction.getStreamer()).thenReturn(streamer);
		
		lenient().when(streamer.getSettings()).thenReturn(streamerSettings);
		lenient().when(streamerSettings.getPredictions()).thenReturn(predictionSettings);
		lenient().when(predictionSettings.getOutcomePicker()).thenReturn(outcomePicker);
		lenient().when(predictionSettings.getAmountCalculator()).thenReturn(amountCalculator);
		
		lenient().when(event.getId()).thenReturn(EVENT_ID);
		lenient().when(event.getStatus()).thenReturn(EventStatus.ACTIVE);
		
		lenient().when(outcome.getId()).thenReturn(OUTCOME_ID);
		
		lenient().when(outcomePicker.chooseOutcome(prediction)).thenReturn(outcome);
		lenient().when(amountCalculator.calculateAmount(prediction, outcome)).thenReturn(AMOUNT);
	}
	
	@Test
	void eventNotActive(){
		when(event.getStatus()).thenReturn(EventStatus.LOCKED);
		
		assertDoesNotThrow(() -> tested.placeBet(prediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
	}
	
	@Test
	void outcomeException() throws BetPlacementException{
		when(outcomePicker.chooseOutcome(prediction)).thenThrow(new BetPlacementException("For tests"));
		
		assertDoesNotThrow(() -> tested.placeBet(prediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
	}
	
	@Test
	void amountException() throws BetPlacementException{
		when(amountCalculator.calculateAmount(prediction, outcome)).thenThrow(new BetPlacementException("For tests"));
		
		assertDoesNotThrow(() -> tested.placeBet(prediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
	}
	
	@Test
	void amountTooLow() throws BetPlacementException{
		when(amountCalculator.calculateAmount(prediction, outcome)).thenReturn(5);
		
		assertDoesNotThrow(() -> tested.placeBet(prediction));
		
		verify(gqlApi, never()).makePrediction(any(), any(), anyInt(), any());
	}
	
	@Test
	void nominal(){
		try(var transactionIdFactory = mockStatic(TransactionIdFactory.class)){
			transactionIdFactory.when(TransactionIdFactory::create).thenReturn(TRANSACTION_ID);
			
			assertDoesNotThrow(() -> tested.placeBet(prediction));
			
			verify(gqlApi).makePrediction(EVENT_ID, OUTCOME_ID, AMOUNT, TRANSACTION_ID);
		}
	}
}