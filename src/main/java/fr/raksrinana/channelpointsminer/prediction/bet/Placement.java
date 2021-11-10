package fr.raksrinana.channelpointsminer.prediction.bet;

import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.handler.data.Prediction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Placement{
	@NotNull
	private Prediction prediction;
	@NotNull
	private Outcome outcome;
	private int amount;
}
