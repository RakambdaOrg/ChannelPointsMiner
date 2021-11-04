package fr.raksrinana.channelpointsminer.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.handler.data.Prediction;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacementException;
import org.jetbrains.annotations.NotNull;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = fr.raksrinana.channelpointsminer.prediction.bet.outcome.MostUsersOutcomePicker.class, name = "mostUsers"),
		@JsonSubTypes.Type(value = fr.raksrinana.channelpointsminer.prediction.bet.outcome.LeastUsersOutcomePicker.class, name = "leastUsers"),
		@JsonSubTypes.Type(value = fr.raksrinana.channelpointsminer.prediction.bet.outcome.MostPointsOutcomePicker.class, name = "mostPoints"),
		@JsonSubTypes.Type(value = LeastPointsOutcomePicker.class, name = "leastPoints"),
		@JsonSubTypes.Type(value = fr.raksrinana.channelpointsminer.prediction.bet.outcome.SmartOutcomePicker.class, name = "smart"),
})
public interface OutcomePicker{
	@NotNull
	Outcome chooseOutcome(@NotNull Prediction prediction) throws BetPlacementException;
}
