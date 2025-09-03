package fr.rakambda.channelpointsminer.miner.prediction.bet;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Placement{
	@NonNull
	private BettingPrediction bettingPrediction;
	@NonNull
	private Outcome outcome;
	private int amount;
}
