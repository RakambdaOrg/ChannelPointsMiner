package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Event{
	@JsonProperty("id")
	@NonNull
	private String id;
	@JsonProperty("channel_id")
	@NonNull
	private String channelId;
	@JsonProperty("created_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NonNull
	private ZonedDateTime createdAt;
	@JsonProperty("ended_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime endedAt;
	@JsonProperty("prediction_window_seconds")
	private int predictionWindowSeconds;
	@JsonProperty("status")
	@NonNull
	private EventStatus status;
	@JsonProperty("title")
	@NonNull
	private String title;
	@JsonProperty("winning_outcome_id")
	@Nullable
	private String winningOutcomeId;
	@JsonProperty("outcomes")
	@NonNull
	@Builder.Default
	private List<Outcome> outcomes = new ArrayList<>();
}
