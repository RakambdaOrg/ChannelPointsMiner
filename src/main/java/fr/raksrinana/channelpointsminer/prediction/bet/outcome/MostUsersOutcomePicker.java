package fr.raksrinana.twitchminer.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.twitchminer.handler.data.Prediction;
import fr.raksrinana.twitchminer.prediction.bet.BetPlacementException;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Comparator;

@JsonTypeName("mostUsers")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@Log4j2
public class MostUsersOutcomePicker implements OutcomePicker{
	@Override
	@NotNull
	public Outcome chooseOutcome(@NotNull Prediction prediction) throws BetPlacementException{
		return prediction.getEvent().getOutcomes().stream()
				.max(Comparator.comparingInt(Outcome::getTotalUsers))
				.orElseThrow(() -> new BetPlacementException("Couldn't get outcome with most users"));
	}
}
