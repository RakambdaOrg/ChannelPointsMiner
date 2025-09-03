package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.predictionupdated;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Prediction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class PredictionUpdatedData{
	@JsonProperty("prediction")
	@NonNull
	private Prediction prediction;
}
