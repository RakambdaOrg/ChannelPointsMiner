package fr.raksrinana.twitchminer.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	@NotNull
	private String id;
	@JsonProperty("channel_id")
	@NotNull
	private String channelId;
	@JsonProperty("created_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime createdAt;
	@JsonProperty("created_by")
	@NotNull
	private By createdBy;
	@JsonProperty("ended_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime endedAt;
	@JsonProperty("ended_by")
	@Nullable
	private By endedBy;
	@JsonProperty("locked_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime lockedAt;
	@JsonProperty("locked_by")
	@Nullable
	private By lockedBy;
	@JsonProperty("prediction_window_seconds")
	private int predictionWindowSeconds;
	@JsonProperty("status")
	@NotNull
	private EventStatus status;
	@JsonProperty("title")
	@NotNull
	private String title;
	@JsonProperty("winning_outcome_id")
	@Nullable
	private String winningOutcomeId;
	@JsonProperty("outcomes")
	@NotNull
	@Builder.Default
	private List<Outcome> outcomes = new ArrayList<>();
}
