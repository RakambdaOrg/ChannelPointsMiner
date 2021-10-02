package fr.raksrinana.twitchminer.api.kraken.data.follows;

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
	@JsonProperty("created_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("channel")
	private Channel channel;
	@JsonProperty("notifications")
	private boolean notifications;
}
