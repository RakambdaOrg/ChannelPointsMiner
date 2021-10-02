package fr.raksrinana.twitchminer.api.helix.data.follows;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
