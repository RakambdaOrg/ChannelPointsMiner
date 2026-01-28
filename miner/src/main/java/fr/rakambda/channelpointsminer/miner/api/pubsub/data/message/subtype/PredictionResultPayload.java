package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class PredictionResultPayload{
	@JsonProperty("type")
	@NonNull
	private PredictionResultType type;
	@Nullable
	@JsonProperty("points_won")
	private Integer pointsWon;
}
