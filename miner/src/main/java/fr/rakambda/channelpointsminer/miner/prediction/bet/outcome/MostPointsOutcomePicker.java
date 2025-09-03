package fr.rakambda.channelpointsminer.miner.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.database.IDatabase;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import java.util.Comparator;

@JsonTypeName("mostPoints")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@Log4j2
@JsonClassDescription("Choose the outcome with the most points. This is the same as 'the outcome with lower odds'.")
public class MostPointsOutcomePicker implements IOutcomePicker{
	@Override
	@NonNull
	public Outcome chooseOutcome(@NonNull BettingPrediction bettingPrediction, @NonNull IDatabase database) throws BetPlacementException{
		return bettingPrediction.getEvent().getOutcomes().stream()
				.max(Comparator.comparingLong(Outcome::getTotalPoints))
				.orElseThrow(() -> new BetPlacementException("Couldn't get outcome with most points"));
	}
}
