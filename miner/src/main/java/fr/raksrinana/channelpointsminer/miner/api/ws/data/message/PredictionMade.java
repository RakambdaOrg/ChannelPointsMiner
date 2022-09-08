package fr.raksrinana.channelpointsminer.miner.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.predictionmade.PredictionMadeData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeName("prediction-made")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionMade extends IPubSubMessage{
	@JsonProperty("data")
	private PredictionMadeData data;
}
