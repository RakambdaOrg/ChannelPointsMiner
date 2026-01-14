package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Prediction{
	@JsonProperty("event_id")
	@NonNull
	private String eventId;
	@JsonProperty("outcome_id")
	@NonNull
	private String outcomeId;
	@JsonProperty("channel_id")
	@NonNull
	private String channelId;
	@JsonProperty("points")
	private int points;
	@JsonProperty("predicted_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NonNull
	private ZonedDateTime predictedAt;
	@JsonProperty("result")
	@Nullable
	private PredictionResultPayload result;
}
