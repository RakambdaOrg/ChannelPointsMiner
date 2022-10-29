package fr.rakambda.channelpointsminer.miner.prediction.bet.amount;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import org.jetbrains.annotations.NotNull;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = ConstantAmount.class, name = "constant"),
		@JsonSubTypes.Type(value = PercentageAmount.class, name = "percentage"),
})
@JsonClassDescription("Prediction amount calculator. The minimum amount that can be placed is 10.")
public interface IAmountCalculator{
	int calculateAmount(@NotNull BettingPrediction bettingPrediction, @NotNull Outcome outcome) throws BetPlacementException;
}
