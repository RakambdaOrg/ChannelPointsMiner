package fr.raksrinana.twitchminer.prediction.bet.amount;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.twitchminer.handler.data.Prediction;
import fr.raksrinana.twitchminer.prediction.bet.BetPlacementException;
import lombok.*;
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
public class PercentageAmount implements AmountCalculator{
	private float percentage;
	private int max;
	
	@Override
	public int calculateAmount(@NotNull Prediction prediction, @NotNull Outcome outcome) throws BetPlacementException{
		var currentPoints = prediction.getStreamer().getChannelPoints().orElseThrow(() -> new BetPlacementException("Failed to get current owned channel points"));
		return (int) Math.min(currentPoints * percentage, max);
	}
}
