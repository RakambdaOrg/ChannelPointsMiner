package fr.raksrinana.channelpointsminer.prediction.bet.action;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Predictor;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacementException;
import fr.raksrinana.channelpointsminer.prediction.bet.Placement;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Comparator;

@JsonTypeName("stealth")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@Log4j2
public class StealthPredictionAction implements IPredictionAction{
	@Override
	public void perform(@NotNull Placement placement) throws BetPlacementException{
		var topPoints = placement.getOutcome().getTopPredictors()
				.stream()
				.max(Comparator.comparingInt(Predictor::getPoints))
				.map(Predictor::getPoints)
				.orElseThrow(() -> new BetPlacementException("No one placed a bet on the outcome we chose, cannot be stealthy"));
		
		if(topPoints <= placement.getAmount()){
			placement.setAmount(topPoints - 1);
		}
	}
}
