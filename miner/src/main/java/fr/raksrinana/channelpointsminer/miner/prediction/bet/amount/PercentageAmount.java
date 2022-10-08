package fr.raksrinana.channelpointsminer.miner.prediction.bet.amount;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("percentage")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@JsonClassDescription("Place a percentage of your points (with a limit).")
public class PercentageAmount implements IAmountCalculator{
	@JsonProperty(value = "percentage", required = true)
	@JsonPropertyDescription("Percentage of your owned points to place, as a decimal value, between 0 and 1.")
	private float percentage;
	@JsonProperty(value = "max", required = true)
	@JsonPropertyDescription("Maximum number of points.")
	private int max;
	
	@Override
	public int calculateAmount(@NotNull BettingPrediction bettingPrediction, @NotNull Outcome outcome) throws BetPlacementException{
		var currentPoints = bettingPrediction.getStreamer().getChannelPoints().orElseThrow(() -> new BetPlacementException("Failed to get current owned channel points"));
		return (int) Math.min(currentPoints * percentage, max);
	}
}
