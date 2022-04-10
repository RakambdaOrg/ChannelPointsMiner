package fr.raksrinana.channelpointsminer.miner.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.BetPlacementException;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.BetUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("kelly")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@Log4j2
public class KellyOutcomePicker implements IOutcomePicker{
	@Override
	@NotNull
	public Outcome chooseOutcome(@NotNull BettingPrediction bettingPrediction) throws BetPlacementException{
		var outcomes = bettingPrediction.getEvent().getOutcomes();
		if(outcomes.size() != 2){
			throw new BetPlacementException("Two outcomes are needed, there was " + outcomes.size());
		}
		
		var outcome1 = outcomes.get(0);
		var outcome2 = outcomes.get(1);
		return BetUtils.getKellyValue(outcome1, outcome2) >= 0 ? outcome1 : outcome2;
	}
}
