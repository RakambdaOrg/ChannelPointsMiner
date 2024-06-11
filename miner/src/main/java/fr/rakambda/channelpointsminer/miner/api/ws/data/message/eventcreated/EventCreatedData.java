package fr.rakambda.channelpointsminer.miner.api.ws.data.message.eventcreated;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class EventCreatedData{
	@JsonProperty("event")
	@NotNull
	private Event event;
}
