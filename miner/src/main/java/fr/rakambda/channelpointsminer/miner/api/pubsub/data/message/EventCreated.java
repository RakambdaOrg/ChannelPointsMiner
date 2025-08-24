package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.eventcreated.EventCreatedData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("event-created")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class EventCreated extends IPubSubMessage{
	@JsonProperty("data")
	private EventCreatedData data;
}
