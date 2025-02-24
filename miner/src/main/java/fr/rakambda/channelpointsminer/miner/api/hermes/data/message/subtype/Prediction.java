package fr.rakambda.channelpointsminer.miner.api.hermes.data.message.subtype;

import java.time.ZonedDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Prediction{
	@JsonProperty("event_id")
	@NotNull
	private String eventId;
	@JsonProperty("outcome_id")
	@NotNull
	private String outcomeId;
	@JsonProperty("channel_id")
	@NotNull
	private String channelId;
	@JsonProperty("points")
	private int points;
	@JsonProperty("predicted_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime predictedAt;
	@JsonProperty("result")
	@Nullable
	private PredictionResultPayload result;
}
