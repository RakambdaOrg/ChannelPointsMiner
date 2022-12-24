package fr.rakambda.channelpointsminer.miner.database.model.prediction;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
public class OutcomeStatistic{
	
	@NotNull
	private final String badge;
	private final int userCnt;
	private final double averageWinRate;
	private final double averageUserBetsPlaced;
	private final double averageUserWins;
	private final double averageReturnOnInvestment;
}

