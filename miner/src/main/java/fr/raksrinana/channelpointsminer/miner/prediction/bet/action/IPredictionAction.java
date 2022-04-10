package fr.raksrinana.channelpointsminer.miner.prediction.bet.action;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.BetPlacementException;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.Placement;
import org.jetbrains.annotations.NotNull;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = StealthPredictionAction.class, name = "stealth"),
})
public interface IPredictionAction{
	void perform(@NotNull Placement placement) throws BetPlacementException;
}
