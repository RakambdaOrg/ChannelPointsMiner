package fr.rakambda.channelpointsminer.miner.prediction.bet.action;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.rakambda.channelpointsminer.miner.prediction.bet.Placement;
import fr.rakambda.channelpointsminer.miner.prediction.bet.exception.BetPlacementException;
import org.jspecify.annotations.NonNull;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = StealthPredictionAction.class, name = "stealth"),
})
@JsonClassDescription("Prediction actions are a way to perform actions / verifications before a bet is placed.")
public interface IPredictionAction{
	void perform(@NonNull Placement placement) throws BetPlacementException;
}
