package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.twitchminer.utils.json.UnknownDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.ZonedDateTime;
import java.util.List;

@JsonTypeName("event-updated")
@Getter
@ToString(callSuper = true)
public class EventUpdated extends Message{
	@JsonProperty("data")
	private Data data;
	
	public EventUpdated(){
		super("event-updated");
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class Data{
		@JsonProperty("timestamp")
		@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
		private ZonedDateTime timestamp;
		@JsonProperty("event")
		private Event event;
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class Event{
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
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class By{
		@JsonProperty("type")
		private ByType type;
		@JsonProperty("user_id")
		private String userId;
		@JsonProperty("user_display_name")
		private String userDisplayName;
		@JsonProperty("extension_client_id")
		private String extensionClientId;
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class Outcome{
		@JsonProperty("id")
		private String id;
		@JsonProperty("color")
		private Color color;
		@JsonProperty("title")
		private String title;
		@JsonProperty("total_points")
		private long totalPoints;
		@JsonProperty("total_users")
		private int totalUsers;
		@JsonProperty("top_predictors")
		private List<Predictor> topPredictors;
		@JsonProperty("badge")
		private Badge badge;
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class Badge{
		@JsonProperty("version")
		private String version;
		@JsonProperty("set_id")
		private String setId;
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class Predictor{
		@JsonProperty("id")
		private String id;
		@JsonProperty("event_id")
		private String eventId;
		@JsonProperty("outcome_id")
		private String outcomeId;
		@JsonProperty("channel_id")
		private String channelId;
		@JsonProperty("points")
		private int points;
		@JsonProperty("predicted_at")
		@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
		private ZonedDateTime predictedAt;
		@JsonProperty("updated_at")
		@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
		private ZonedDateTime updatedAt;
		@JsonProperty("user_id")
		private String userId;
		@JsonProperty("result")
		@JsonDeserialize(using = UnknownDeserializer.class)
		private Object result;
		@JsonProperty("user_display_name")
		private String userDisplayName;
	}
	
	enum ByType{
		USER
	}
	
	enum Status{
		ACTIVE
	}
	
	enum Color{
		BLUE,
		PINK
	}
}
