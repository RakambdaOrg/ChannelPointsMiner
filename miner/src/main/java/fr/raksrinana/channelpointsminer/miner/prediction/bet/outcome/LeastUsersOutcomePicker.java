package fr.raksrinana.channelpointsminer.miner.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Comparator;

@JsonTypeName("leastUsers")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@Log4j2
@JsonClassDescription("Choose the outcome with the least users.")
public class LeastUsersOutcomePicker implements IOutcomePicker{
	@Override
	@NotNull
	public Outcome chooseOutcome(@NotNull BettingPrediction bettingPrediction, @NotNull IDatabase database) throws BetPlacementException{
		return bettingPrediction.getEvent().getOutcomes().stream()
				.min(Comparator.comparingInt(Outcome::getTotalUsers))
				.orElseThrow(() -> new BetPlacementException("Couldn't get outcome with least users"));
	}
}
