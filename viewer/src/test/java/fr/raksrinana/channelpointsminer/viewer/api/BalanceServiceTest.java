package fr.raksrinana.channelpointsminer.viewer.api;

import fr.raksrinana.channelpointsminer.viewer.api.data.BalanceData;
import fr.raksrinana.channelpointsminer.viewer.repository.BalanceRepository;
import fr.raksrinana.channelpointsminer.viewer.repository.entity.BalanceEntity;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest{
    private static final String CHANNEL_ID = "CID";
    
    private static final int BALANCE_ID1 = 1;
    private static final int BALANCE_AMOUNT1 = 100;
    private static final Instant BALANCE_DATE1 = Instant.parse("2022-04-15T19:45:30.000Z");
    
    private static final int BALANCE_ID2 = 2;
    private static final int BALANCE_AMOUNT2 = 200;
    private static final Instant BALANCE_DATE2 = Instant.parse("2022-04-15T19:46:30.000Z");
    
    @InjectMocks
    private BalanceService tested;
    
    @Mock
    private BalanceRepository balanceRepository;
    
    @Mock
    private BalanceEntity balanceEntity1;
    @Mock
    private BalanceEntity balanceEntity2;
    
    @BeforeEach
    void setUp(){
        lenient().when(balanceEntity1.getId()).thenReturn(BALANCE_ID1);
        lenient().when(balanceEntity1.getChannelId()).thenReturn(CHANNEL_ID);
        lenient().when(balanceEntity1.getBalance()).thenReturn(BALANCE_AMOUNT1);
        lenient().when(balanceEntity1.getBalanceDate()).thenReturn(BALANCE_DATE1);
        
        lenient().when(balanceEntity2.getId()).thenReturn(BALANCE_ID2);
        lenient().when(balanceEntity2.getChannelId()).thenReturn(CHANNEL_ID);
        lenient().when(balanceEntity2.getBalance()).thenReturn(BALANCE_AMOUNT2);
        lenient().when(balanceEntity2.getBalanceDate()).thenReturn(BALANCE_DATE2);
    }
    
    @Test
    void testGetAllBalance(){
        when(balanceRepository.findAllByChannelId(CHANNEL_ID)).thenReturn(List.of(balanceEntity1, balanceEntity2));
        
        assertThat(tested.getAllBalance(CHANNEL_ID)).containsExactlyInAnyOrder(
                BalanceData.builder().balance(BALANCE_AMOUNT1).date(BALANCE_DATE1).build(),
                BalanceData.builder().balance(BALANCE_AMOUNT2).date(BALANCE_DATE2).build()
        );
    }
}