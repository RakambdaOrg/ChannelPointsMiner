package fr.rakambda.channelpointsminer.miner.prediction.bet.outcome;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.database.IDatabase;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import org.jetbrains.annotations.NotNull;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = MostUsersOutcomePicker.class, name = "mostUsers"),
		@JsonSubTypes.Type(value = LeastUsersOutcomePicker.class, name = "leastUsers"),
		@JsonSubTypes.Type(value = MostPointsOutcomePicker.class, name = "mostPoints"),
		@JsonSubTypes.Type(value = LeastPointsOutcomePicker.class, name = "leastPoints"),
		@JsonSubTypes.Type(value = SmartOutcomePicker.class, name = "smart"),
		@JsonSubTypes.Type(value = BiggestPredictorOutcomePicker.class, name = "biggestPredictor"),
		@JsonSubTypes.Type(value = MostTrustedPicker.class, name = "mostTrusted"),
})
@JsonClassDescription("Prediction outcome picker")
public interface IOutcomePicker{
	@NotNull
	Outcome chooseOutcome(@NotNull BettingPrediction bettingPrediction, @NotNull IDatabase database) throws BetPlacementException;
}
