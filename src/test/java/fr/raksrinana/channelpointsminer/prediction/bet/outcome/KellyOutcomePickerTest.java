package fr.raksrinana.channelpointsminer.prediction.bet.outcome;

import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacementException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KellyOutcomePickerTest{
    private final KellyOutcomePicker tested = KellyOutcomePicker.builder().build();
    
    @Mock
    private BettingPrediction bettingPrediction;
    @Mock
    private Event event;
    @Mock
    private Outcome blueOutcome;
    @Mock
    private Outcome pinkOutcome;
    
    @BeforeEach
    void setUp(){
        lenient().when(bettingPrediction.getEvent()).thenReturn(event);
        lenient().when(event.getOutcomes()).thenReturn(List.of(blueOutcome, pinkOutcome));
    }
    
    @Test
    void chose() throws BetPlacementException{
        when(blueOutcome.getTotalUsers()).thenReturn(150);
        when(blueOutcome.getTotalPoints()).thenReturn(50L);
        when(pinkOutcome.getTotalUsers()).thenReturn(50);
        when(pinkOutcome.getTotalPoints()).thenReturn(100L);
        
        assertThat(tested.chooseOutcome(bettingPrediction)).isEqualTo(blueOutcome);
    }
    
    @Test
    void chose2() throws BetPlacementException{
        when(blueOutcome.getTotalUsers()).thenReturn(150);
        when(blueOutcome.getTotalPoints()).thenReturn(1000L);
        when(pinkOutcome.getTotalUsers()).thenReturn(50);
        when(pinkOutcome.getTotalPoints()).thenReturn(50L);
        
        assertThat(tested.chooseOutcome(bettingPrediction)).isEqualTo(pinkOutcome);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {
            0,
            1,
            3,
            4
    })
    void wrongNumberOutcomes(int count){
        var outcomes = new ArrayList<Outcome>();
        for(int i = 0; i < count; i++){
            outcomes.add(mock(Outcome.class));
        }
        when(event.getOutcomes()).thenReturn(outcomes);
        
        assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(bettingPrediction));
    }
    
    @Test
    void noBetsOnFirstOutcome(){
        when(blueOutcome.getTotalPoints()).thenReturn(0L);
        
        assertThrows(BetPlacementException.class, () -> tested.chooseOutcome(bettingPrediction));
    }
}