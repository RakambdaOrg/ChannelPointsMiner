package fr.raksrinana.twitchminer.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.twitchminer.handler.data.Prediction;
import fr.raksrinana.twitchminer.prediction.bet.BetPlacementException;
import org.jetbrains.annotations.NotNull;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = MostUsersOutcomePicker.class, name = "mostUsers"),
		@JsonSubTypes.Type(value = LeastUsersOutcomePicker.class, name = "leastUsers"),
		@JsonSubTypes.Type(value = MostPointsOutcomePicker.class, name = "mostPoints"),
		@JsonSubTypes.Type(value = LeastPointsOutcomePicker.class, name = "leastPoints"),
})
public interface OutcomePicker{
	@NotNull
	Outcome chooseOutcome(@NotNull Prediction prediction) throws BetPlacementException;
}
