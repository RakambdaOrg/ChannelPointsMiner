package fr.rakambda.channelpointsminer.miner.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.database.IDatabase;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.NotEnoughUsersBetPlacementException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@JsonTypeName("smart")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@JsonClassDescription("Choose the outcome with the most users. However, if the two most picked outcomes have a user count similar, choose the outcome with the least points (higher odds).")
public class SmartOutcomePicker implements IOutcomePicker{
	@JsonProperty(value = "percentageGap", required = true)
	@JsonPropertyDescription("The percent gap of the user count, as decimal, between 0 and 1. (i.e. Setting this to 0.1, will mean that the condition switches states when the difference between sides is 10%, so 45% of the users on one side and 55% on the other).")
	private float percentageGap;
	
	@Override
	@NonNull
	public Outcome chooseOutcome(@NonNull BettingPrediction bettingPrediction, @NonNull IDatabase database) throws BetPlacementException{
		
		var totalUsers = (double) bettingPrediction.getEvent().getOutcomes().stream().mapToInt(Outcome::getTotalUsers).sum();
		if(Double.compare(0D, totalUsers) == 0){
			throw new NotEnoughUsersBetPlacementException(0);
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
