package fr.raksrinana.twitchminer.api.helix.data.follows;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
public class Follow{
	@JsonProperty("from_id")
	private String fromId;
	@JsonProperty("from_login")
	private String fromLogin;
	@JsonProperty("from_name")
	private String fromName;
	@JsonProperty("to_id")
	private String toId;
	@JsonProperty("to_name")
	private String toName;
	@JsonProperty("followed_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime followedAt;
}
