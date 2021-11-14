package fr.raksrinana.channelpointsminer.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Predictor;
import fr.raksrinana.channelpointsminer.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacementException;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("biggestPredictor")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@Log4j2
public class BiggestPredictorOutcomePicker implements IOutcomePicker{
	@Override
	@NotNull
	public Outcome chooseOutcome(@NotNull BettingPrediction bettingPrediction) throws BetPlacementException{
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
