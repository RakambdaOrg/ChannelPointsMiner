package fr.rakambda.channelpointsminer.miner.streamer;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import fr.rakambda.channelpointsminer.miner.prediction.bet.action.IPredictionAction;
import fr.rakambda.channelpointsminer.miner.prediction.bet.amount.IAmountCalculator;
import fr.rakambda.channelpointsminer.miner.prediction.bet.amount.PercentageAmount;
import fr.rakambda.channelpointsminer.miner.prediction.bet.outcome.IOutcomePicker;
import fr.rakambda.channelpointsminer.miner.prediction.bet.outcome.SmartOutcomePicker;
import fr.rakambda.channelpointsminer.miner.prediction.delay.FromEndDelay;
import fr.rakambda.channelpointsminer.miner.prediction.delay.IDelayCalculator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonClassDescription("Prediction settings")
public class PredictionSettings{
	@JsonProperty("delayCalculator")
	@JsonPropertyDescription("How to calculate when to place the bet. Default: 10s from end")
	@NotNull
	@Builder.Default
	private IDelayCalculator delayCalculator = FromEndDelay.builder().seconds(10).build();
	@JsonProperty("minimumPointsRequired")
	@JsonPropertyDescription("Minimum amount of points to have to place a bet. If this threshold is not reached, no bet is placed. Default: fromEnd(10)")
	@Builder.Default
	private int minimumPointsRequired = 0;
	@JsonProperty("outcomePicker")
	@JsonPropertyDescription("How to choose what outcome to place the bet on. Default: smart(0.2)")
	@NotNull
	@Builder.Default
	private IOutcomePicker outcomePicker = SmartOutcomePicker.builder().percentageGap(.2f).build();
	@JsonProperty("amountCalculator")
	@JsonPropertyDescription("How to calculate the amount to the bet. Default: percentage(percentage: 20, max: 50000)")
	@NotNull
	@Builder.Default
	private IAmountCalculator amountCalculator = PercentageAmount.builder().percentage(.2F).max(50_000).build();
	@JsonProperty("actions")
	@JsonPropertyDescription("Actions to perform before a bet is placed.")
	@NotNull
	@Builder.Default
	private List<IPredictionAction> actions = new ArrayList<>();
	
	public PredictionSettings(PredictionSettings origin){
		delayCalculator = origin.delayCalculator;
		minimumPointsRequired = origin.minimumPointsRequired;
		outcomePicker = origin.outcomePicker;
		amountCalculator = origin.amountCalculator;
		actions = origin.actions;
	}
}
