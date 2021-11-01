package fr.raksrinana.twitchminer.prediction.bet.amount;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.twitchminer.handler.data.Prediction;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("mostUsers")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class ConstantAmount implements AmountCalculator{
	private int amount;
	
	@Override
	public int calculateAmount(@NotNull Prediction prediction, @NotNull Outcome outcome){
		return amount;
	}
}
