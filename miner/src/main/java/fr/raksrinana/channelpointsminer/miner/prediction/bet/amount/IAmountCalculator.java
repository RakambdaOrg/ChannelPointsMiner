package fr.raksrinana.channelpointsminer.miner.prediction.bet.amount;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.BetPlacementException;
import org.jetbrains.annotations.NotNull;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = ConstantAmount.class, name = "constant"),
		@JsonSubTypes.Type(value = PercentageAmount.class, name = "percentage"),
})
public interface IAmountCalculator{
	int calculateAmount(@NotNull BettingPrediction bettingPrediction, @NotNull Outcome outcome) throws BetPlacementException;
}
