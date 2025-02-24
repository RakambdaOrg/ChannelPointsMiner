package fr.rakambda.channelpointsminer.miner.api.hermes.data.message.subtype;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
	@JsonProperty("ended_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime endedAt;
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
