package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.util.json.TwitchTimestampDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

@JsonTypeName("commercial")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Commercial extends Message{
	@JsonProperty("server_time")
	@JsonDeserialize(using = TwitchTimestampDeserializer.class)
	@NotNull
	private Instant serverTime;
	@JsonProperty("length")
	private int length;
	@JsonProperty("scheduled")
	private boolean scheduled;
}
