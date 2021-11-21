package fr.raksrinana.channelpointsminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionresult.PredictionResultData;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("prediction-result")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionResult extends IMessage{
	@JsonProperty("data")
	@NotNull
	private PredictionResultData data;
}
