package fr.rakambda.channelpointsminer.miner.api.ws.data.message.eventcreated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class EventCreatedData{
	@JsonProperty("event")
	@NotNull
	private Event event;
}
