package fr.raksrinana.channelpointsminer.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class PredictionResultPayload{
	@JsonProperty("type")
	@NotNull
	private PredictionResultType type;
	@JsonProperty("points_won")
	private int pointsWon;
	@JsonProperty("is_acknowledged")
	private boolean isAcknowledged;
}
