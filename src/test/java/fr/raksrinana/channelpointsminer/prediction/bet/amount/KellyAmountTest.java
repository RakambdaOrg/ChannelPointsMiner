package fr.raksrinana.channelpointsminer.prediction.bet.amount;

import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacementException;
import fr.raksrinana.channelpointsminer.prediction.bet.BetUtils;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KellyAmountTest{
	private static final int MAX = 20;
	private static final float PERCENTAGE = 0.25F;
	private static final int STREAMER_POINTS = 100;
	
	private final KellyAmount tested = KellyAmount.builder().percentage(PERCENTAGE).max(MAX).build();
	
	@Mock
	private BettingPrediction bettingPrediction;
	@Mock
	private Event event;
	@Mock
	private Outcome outcome;
	@Mock
	private Outcome outcome2;
	@Mock
    private Streamer streamer;
    
    @BeforeEach
    void setUp(){
        lenient().when(bettingPrediction.getStreamer()).thenReturn(streamer);
        lenient().when(bettingPrediction.getEvent()).thenReturn(event);
        lenient().when(event.getOutcomes()).thenReturn(List.of(outcome, outcome2));
        lenient().when(streamer.getChannelPoints()).thenReturn(Optional.of(STREAMER_POINTS));
    }
    
    @Test
    void calculateUnderMax() throws BetPlacementException{
	    try(var betUtils = mockStatic(BetUtils.class)){
		    betUtils.when(() -> BetUtils.getKellyValue(outcome, outcome2)).thenReturn(0.5F);
		
		    assertThat(tested.calculateAmount(bettingPrediction, outcome)).isEqualTo(12);
	    }
    }
    
    @Test
    void calculateOverMax() throws BetPlacementException{
	    try(var betUtils = mockStatic(BetUtils.class)){
		    betUtils.when(() -> BetUtils.getKellyValue(outcome, outcome2)).thenReturn(1000F);
		
		    assertThat(tested.calculateAmount(bettingPrediction, outcome)).isEqualTo(MAX);
	    }
    }
    
    @Test
    void calculateMissingOtherOutcome(){
        when(event.getOutcomes()).thenReturn(List.of(outcome));
        
        assertThrows(BetPlacementException.class, () -> tested.calculateAmount(bettingPrediction, outcome));
    }
    
    @Test
    void calculateUnknownPoints(){
        lenient().when(streamer.getChannelPoints()).thenReturn(Optional.empty());
        
        assertThrows(BetPlacementException.class, () -> tested.calculateAmount(bettingPrediction, outcome));
    }
}