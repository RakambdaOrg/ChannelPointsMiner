package fr.rakambda.channelpointsminer.miner.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.database.IDatabase;
import fr.rakambda.channelpointsminer.miner.database.NoOpDatabase;
import fr.rakambda.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import java.util.Comparator;

@JsonTypeName("mostTrusted")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@JsonClassDescription("Choose the outcome that's backed by other users with the highest average return-on-investment. Requires analytics to be enabled and recordUserPredictions to be activated.")
public class MostTrustedPicker implements IOutcomePicker{
	
	@JsonProperty("minTotalBetsPlacedByUser")
	@JsonPropertyDescription("Only user with at least this number of bets are considered in the calculation. Default: 5")
	@Builder.Default
	private int minTotalBetsPlacedByUser = 5;
	@JsonProperty("minTotalBetsPlacedOnPrediction")
	@JsonPropertyDescription("Need at least x bets placed to bet on this prediction. Default: 10")
	@Builder.Default
	private int minTotalBetsPlacedOnPrediction = 10;
	@JsonProperty("minTotalBetsPlacedOnOutcome")
	@JsonPropertyDescription("Need at least x bets placed the chosen outcome. Default: 5")
	@Builder.Default
	private int minTotalBetsPlacedOnOutcome = 5;
	
	@Override
	@NonNull
	public Outcome chooseOutcome(@NonNull BettingPrediction bettingPrediction, @NonNull IDatabase database) throws BetPlacementException{
		
		try{
			if(database instanceof NoOpDatabase){
				throw new BetPlacementException("A database needs to be configured for this outcome picker to work");
			}
			
			var outcomes = bettingPrediction.getEvent().getOutcomes();
			var title = bettingPrediction.getEvent().getTitle();
			
			var outcomeStatistics = database.getOutcomeStatisticsForChannel(bettingPrediction.getEvent().getChannelId(), minTotalBetsPlacedByUser);
			
			var mostTrusted = outcomeStatistics.stream()
					.max(Comparator.comparingDouble(OutcomeStatistic::getAverageReturnOnInvestment))
					.orElseThrow(() -> new BetPlacementException("No outcome statistics found. Maybe not enough data gathered yet."));
			
			for(var outcomeStats : outcomeStatistics){
				log.info("Outcome stats for '{}': {}", outcomeStats.getBadge(), outcomeStats.toString());
			}
			
			int totalBetsPlaced = outcomeStatistics.stream().mapToInt(OutcomeStatistic::getUserCnt).sum();
			if(totalBetsPlaced < minTotalBetsPlacedOnPrediction){
				throw new BetPlacementException("Not enough bets placed for prediction %s. Minimum is %d. Was %d".formatted(title, minTotalBetsPlacedOnPrediction, totalBetsPlaced));
			}
			
			var chosenOutcome = outcomes.stream()
					.filter(o -> o.getBadge().getVersion().equalsIgnoreCase(mostTrusted.getBadge()))
					.findAny()
					.orElseThrow(() -> new BetPlacementException("Outcome badge not found: %s".formatted(mostTrusted.getBadge())));
			
			if(mostTrusted.getUserCnt() < minTotalBetsPlacedOnOutcome){
				throw new BetPlacementException(
						"Not enough bets placed on chosen outcome: '%s'. Minimum is %d. Was %d".formatted(chosenOutcome.getTitle(), minTotalBetsPlacedOnOutcome, mostTrusted.getUserCnt()));
			}
			
			log.info("Prediction: '{}'. Most trusted outcome (highest average return of investment of other bettors): Title: '{}', Badge: {}.",
					title, chosenOutcome.getTitle(), chosenOutcome.getBadge().getVersion());
			
			return chosenOutcome;
		}
		catch(BetPlacementException e){
			throw e;
		}
		catch(Exception e){
			throw new BetPlacementException("Bet placement failed", e);
		}
	}
}