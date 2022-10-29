package fr.rakambda.channelpointsminer.miner.prediction.bet.amount;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class PercentageAmountTest{
	private static final int MAX = 50;
	private static final float PERCENTAGE = 0.25F;
	private static final int STREAMER_POINTS = 100;
	
	private final PercentageAmount tested = PercentageAmount.builder().percentage(PERCENTAGE).max(MAX).build();
	
	@Mock
	private BettingPrediction bettingPrediction;
	@Mock
	private Outcome outcome;
	@Mock
	private Streamer streamer;
	
	@BeforeEach
	void setUp(){
		lenient().when(bettingPrediction.getStreamer()).thenReturn(streamer);
		lenient().when(streamer.getChannelPoints()).thenReturn(Optional.of(STREAMER_POINTS));
	}
	
	@Test
	void calculateUnderMax() throws BetPlacementException{
		assertThat(tested.calculateAmount(bettingPrediction, outcome)).isEqualTo(25);
	}
	
	@Test
	void calculateOverMax() throws BetPlacementException{
		lenient().when(streamer.getChannelPoints()).thenReturn(Optional.of(1000));
		
		assertThat(tested.calculateAmount(bettingPrediction, outcome)).isEqualTo(MAX);
	}
	
	@Test
	void calculateUnknownPoints(){
		lenient().when(streamer.getChannelPoints()).thenReturn(Optional.empty());
		
		assertThrows(BetPlacementException.class, () -> tested.calculateAmount(bettingPrediction, outcome));
	}
}
