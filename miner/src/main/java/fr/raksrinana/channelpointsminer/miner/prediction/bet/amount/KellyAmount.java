package fr.raksrinana.channelpointsminer.miner.prediction.bet.amount;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.BetPlacementException;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.BetUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@JsonTypeName("kelly")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class KellyAmount implements IAmountCalculator{
	@Builder.Default
	private float percentage = 1;
	private int max;
	
	@Override
	public int calculateAmount(@NotNull BettingPrediction bettingPrediction, @NotNull Outcome outcome) throws BetPlacementException{
		var otherOutcome = bettingPrediction.getEvent().getOutcomes().stream()
				.filter(o -> !Objects.equals(o, outcome))
				.findFirst().orElseThrow(() -> new BetPlacementException("Couldn't find second outcome to calculate Kelly value"));
		
		var currentPoints = bettingPrediction.getStreamer().getChannelPoints().orElseThrow(() -> new BetPlacementException("Failed to get current owned channel points"));
		var kellyAmount = Math.max(1f, currentPoints * BetUtils.getKellyValue(outcome, otherOutcome));
		return (int) Math.min(kellyAmount * percentage, max);
	}
}
