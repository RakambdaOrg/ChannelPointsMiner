package fr.rakambda.channelpointsminer.miner.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Predictor;
import fr.rakambda.channelpointsminer.miner.database.IDatabase;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("biggestPredictor")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@Log4j2
@JsonClassDescription("Choose the outcome with the biggest predictor.")
public class BiggestPredictorOutcomePicker implements IOutcomePicker{
	@Override
	@NotNull
	public Outcome chooseOutcome(@NotNull BettingPrediction bettingPrediction, @NotNull IDatabase database) throws BetPlacementException{
		return bettingPrediction.getEvent().getOutcomes().stream()
				.max(this::compare)
				.orElseThrow(() -> new BetPlacementException("Couldn't get outcome with biggest predictor points"));
	}
	
	private int compare(@NotNull Outcome o1, @NotNull Outcome o2){
		var biggest1 = o1.getTopPredictors().stream().mapToInt(Predictor::getPoints).max().orElse(0);
		var biggest2 = o2.getTopPredictors().stream().mapToInt(Predictor::getPoints).max().orElse(0);
		return Integer.compare(biggest1, biggest2);
	}
}
