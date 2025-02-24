package fr.rakambda.channelpointsminer.miner.api.hermes.data.message.eventupdated;

import java.time.ZonedDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class EventUpdatedData{
	@JsonProperty("timestamp")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime timestamp;
	@JsonProperty("event")
	@NotNull
	private Event event;
}
