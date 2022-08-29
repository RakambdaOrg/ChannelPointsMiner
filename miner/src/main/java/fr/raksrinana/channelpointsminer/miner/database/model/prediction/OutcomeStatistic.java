package fr.raksrinana.channelpointsminer.miner.database.model.prediction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OutcomeStatistic{
    
    @NotNull
    private String badge;
    private int userCnt;
    private double averageWinRate;
    private double averageUserBetsPlaced;
    private double averageUserWins;
    private double averageReturnOnInvestment;
}
