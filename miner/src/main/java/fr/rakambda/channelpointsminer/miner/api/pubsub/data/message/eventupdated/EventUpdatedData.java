package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.eventupdated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class EventUpdatedData{
	@JsonProperty("timestamp")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NonNull
	private ZonedDateTime timestamp;
	@JsonProperty("event")
	@NonNull
	private Event event;
}
