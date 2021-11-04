package fr.raksrinana.channelpointsminer.api.ws.data.message.predictionmade;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.util.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.channelpointsminer.util.json.UnknownDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Prediction{
	@JsonProperty("id")
	@NotNull
	private String id;
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
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime updatedAt;
	@JsonProperty("user_id")
	@NotNull
	private String userId;
	@JsonProperty("result")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object result;
	@JsonProperty("user_display_name")
	@Nullable
	private String userDisplayName;
}
