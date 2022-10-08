package fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class Summary{
	@JsonProperty("count")
	private int count;
	@JsonProperty("last_read_all")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime lastReadAll;
	@JsonProperty("last_seen")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime lastSeen;
}
