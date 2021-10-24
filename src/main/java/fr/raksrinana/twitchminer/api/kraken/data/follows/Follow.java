package fr.raksrinana.twitchminer.api.kraken.data.follows;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Follow{
	@JsonProperty("created_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime createdAt;
	@JsonProperty("channel")
	@NotNull
	private Channel channel;
	@JsonProperty("notifications")
	private boolean notifications;
}
