package fr.raksrinana.channelpointsminer.prediction.bet.amount;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.handler.data.Prediction;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacementException;
import org.jetbrains.annotations.NotNull;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = fr.raksrinana.channelpointsminer.prediction.bet.amount.ConstantAmount.class, name = "constant"),
		@JsonSubTypes.Type(value = fr.raksrinana.channelpointsminer.prediction.bet.amount.PercentageAmount.class, name = "percentage"),
})
public interface AmountCalculator{
	int calculateAmount(@NotNull Prediction prediction, @NotNull Outcome outcome) throws BetPlacementException;
}
