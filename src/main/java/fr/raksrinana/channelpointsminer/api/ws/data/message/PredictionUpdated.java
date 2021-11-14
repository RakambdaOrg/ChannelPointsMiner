package fr.raksrinana.channelpointsminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionupdated.PredictionUpdatedData;
import lombok.*;

@JsonTypeName("prediction-updated")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionUpdated extends IMessage{
	@JsonProperty("data")
	private PredictionUpdatedData data;
}
