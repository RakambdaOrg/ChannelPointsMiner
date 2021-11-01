package fr.raksrinana.twitchminer.streamer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.prediction.bet.amount.AmountCalculator;
import fr.raksrinana.twitchminer.prediction.bet.amount.PercentageAmount;
import fr.raksrinana.twitchminer.prediction.bet.outcome.MostUsersOutcomePicker;
import fr.raksrinana.twitchminer.prediction.bet.outcome.OutcomePicker;
import fr.raksrinana.twitchminer.prediction.delay.DelayCalculator;
import fr.raksrinana.twitchminer.prediction.delay.FromEndDelay;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class PredictionSettings{
	@JsonProperty("delayCalculator")
	@NotNull
	@Builder.Default
	private DelayCalculator delayCalculator = FromEndDelay.builder().seconds(10).build();
	@JsonProperty("minimumPointsRequired")
	private int minimumPointsRequired = 0;
	@JsonProperty("outcomePicker")
	@NotNull
	@Builder.Default
	private OutcomePicker outcomePicker = MostUsersOutcomePicker.builder().build();
	@JsonProperty("amountCalculator")
	@NotNull
	@Builder.Default
	private AmountCalculator amountCalculator = PercentageAmount.builder().percentage(.2F).max(50_000).build();
	
	public PredictionSettings(PredictionSettings origin){
		delayCalculator = origin.delayCalculator;
		minimumPointsRequired = origin.minimumPointsRequired;
		outcomePicker = origin.outcomePicker;
		amountCalculator = origin.amountCalculator;
	}
}
