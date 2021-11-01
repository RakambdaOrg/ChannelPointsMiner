package fr.raksrinana.twitchminer.prediction.bet.amount;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.twitchminer.handler.data.Prediction;
import fr.raksrinana.twitchminer.prediction.bet.BetPlacementException;
import org.jetbrains.annotations.NotNull;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = ConstantAmount.class, name = "constant"),
		@JsonSubTypes.Type(value = PercentageAmount.class, name = "percentage"),
})
public interface AmountCalculator{
	int calculateAmount(@NotNull Prediction prediction, @NotNull Outcome outcome) throws BetPlacementException;
}
