package fr.rakambda.channelpointsminer.miner.prediction.bet;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Placement{
	@NotNull
	private BettingPrediction bettingPrediction;
	@NotNull
	private Outcome outcome;
	private int amount;
}
