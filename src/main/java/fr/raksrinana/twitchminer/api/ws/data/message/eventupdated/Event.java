package fr.raksrinana.twitchminer.api.ws.data.message.eventupdated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Event{
	@JsonProperty("id")
	private String id;
	@JsonProperty("channel_id")
	private String channelId;
	@JsonProperty("created_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("created_by")
	private By createdBy;
	@JsonProperty("ended_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime endedAt;
	@JsonProperty("ended_by")
	private By endedBy;
	@JsonProperty("locked_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime lockedAt;
	@JsonProperty("locked_by")
	private By lockedBy;
	@JsonProperty("prediction_window_seconds")
	private Integer predictionWindowSeconds;
	@JsonProperty("status")
	private Status status;
	@JsonProperty("title")
	private String title;
	@JsonProperty("winning_outcome_id")
	private String winningOutcomeId;
	@JsonProperty("outcomes")
	private List<Outcome> outcomes;
}
