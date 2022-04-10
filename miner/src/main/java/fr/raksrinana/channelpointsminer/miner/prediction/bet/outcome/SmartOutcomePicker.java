package fr.raksrinana.channelpointsminer.miner.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.OutcomeColor;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.BetPlacementException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

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
		var blueOutcome = getOutcome(bettingPrediction, OutcomeColor.BLUE);
		var pinkOutcome = getOutcome(bettingPrediction, OutcomeColor.PINK);
		
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
	private Outcome getOutcome(@NotNull BettingPrediction bettingPrediction, @NotNull OutcomeColor color) throws BetPlacementException{
		return bettingPrediction.getEvent().getOutcomes().stream()
				.filter(o -> o.getColor() == color)
				.findFirst()
				.orElseThrow(() -> new BetPlacementException("Failed to get outcome with color " + color));
	}
}
