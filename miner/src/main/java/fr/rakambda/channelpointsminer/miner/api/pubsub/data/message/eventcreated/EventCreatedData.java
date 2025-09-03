package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.eventcreated;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class EventCreatedData{
	@JsonProperty("event")
	@NonNull
	private Event event;
}
