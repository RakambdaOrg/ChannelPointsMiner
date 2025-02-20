package fr.rakambda.channelpointsminer.miner.prediction.bet.amount;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Outcome;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("constant")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@JsonClassDescription("Always bet the same amount.")
public class ConstantAmount implements IAmountCalculator{
	@JsonProperty(value = "amount", required = true)
	@JsonPropertyDescription("Amount to place.")
	private int amount;
	
	@Override
	public int calculateAmount(@NotNull BettingPrediction bettingPrediction, @NotNull Outcome outcome){
		return amount;
	}
}
