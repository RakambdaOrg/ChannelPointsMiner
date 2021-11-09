package fr.raksrinana.channelpointsminer.api.ws.data.message.predictionupdated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Prediction;
import fr.raksrinana.channelpointsminer.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class PredictionUpdatedData{
	@JsonProperty("timestamp")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime timestamp;
	@JsonProperty("prediction")
	@NotNull
	private Prediction prediction;
}
