package fr.raksrinana.twitchminer.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.OutcomeColor;
import fr.raksrinana.twitchminer.handler.data.Prediction;
import fr.raksrinana.twitchminer.prediction.bet.BetPlacementException;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.twitchminer.api.ws.data.message.subtype.OutcomeColor.BLUE;
import static fr.raksrinana.twitchminer.api.ws.data.message.subtype.OutcomeColor.PINK;

@JsonTypeName("smart")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@Log4j2
public class SmartOutcomePicker implements OutcomePicker{
	private float percentageGap;
	
	@Override
	@NotNull
	public Outcome chooseOutcome(@NotNull Prediction prediction) throws BetPlacementException{
		var blueOutcome = getOutcome(prediction, BLUE);
		var pinkOutcome = getOutcome(prediction, PINK);
		
		var totalUsers = (float) (blueOutcome.getTotalUsers() + pinkOutcome.getTotalUsers());
		var percentageBlue = blueOutcome.getTotalUsers() / totalUsers;
		var percentagePink = pinkOutcome.getTotalUsers() / totalUsers;
		var percentageDiff = Math.abs(percentagePink - percentageBlue);
		
		if(Float.compare(percentageDiff, percentageGap) < 0){
			return blueOutcome.getTotalPoints() <= pinkOutcome.getTotalPoints() ? blueOutcome : pinkOutcome;
		}
		
		return blueOutcome.getTotalUsers() >= pinkOutcome.getTotalUsers() ? blueOutcome : pinkOutcome;
	}
	
	@NotNull
	private Outcome getOutcome(@NotNull Prediction prediction, @NotNull OutcomeColor color) throws BetPlacementException{
		return prediction.getEvent().getOutcomes().stream()
				.filter(o -> o.getColor() == color)
				.findFirst()
				.orElseThrow(() -> new BetPlacementException("Failed to get outcome with color " + color));
	}
}
