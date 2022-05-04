package fr.raksrinana.channelpointsminer.miner.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.BetPlacementException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@JsonTypeName("smart")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@Log4j2
public class SmartOutcomePicker implements IOutcomePicker{
	private float percentageGap;
	
	@Override
	@NotNull
	public Outcome chooseOutcome(@NotNull BettingPrediction bettingPrediction) throws BetPlacementException{
		
		var totalUsers = (double) bettingPrediction.getEvent().getOutcomes().stream().mapToInt(Outcome::getTotalUsers).sum();
		if(Double.compare(0D, totalUsers) == 0){
			throw new BetPlacementException("0 user participated, can't decide which side to place the bet onto");
		}
		
		var percentages = bettingPrediction.getEvent().getOutcomes().stream().collect(Collectors.toMap(o -> o, o -> o.getTotalUsers() / totalUsers));
		
		var bestPercentages = percentages.entrySet().stream()
				.sorted(Comparator.<Map.Entry<Outcome, Double>> comparingDouble(Map.Entry::getValue).reversed())
				.limit(2).toList();
		
		if(bestPercentages.size() < 2){
			throw new BetPlacementException("There's less than 2 options in the bet, can't decide which side to pick");
		}
		
		var bestPercentage1 = bestPercentages.get(0);
		var bestPercentage2 = bestPercentages.get(1);
		
		var percentageDiff = Math.abs(bestPercentage1.getValue() - bestPercentage2.getValue());
		
		if(Double.compare(percentageDiff, percentageGap) < 0){
			return bestPercentage2.getKey().getTotalPoints() <= bestPercentage1.getKey().getTotalPoints() ? bestPercentage2.getKey() : bestPercentage1.getKey();
		}
		
		return bestPercentage2.getKey().getTotalUsers() >= bestPercentage1.getKey().getTotalUsers() ? bestPercentage2.getKey() : bestPercentage1.getKey();
	}
}
