package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.eventupdated.EventUpdatedData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("event-updated")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class EventUpdated extends IPubSubMessage{
	@JsonProperty("data")
	private EventUpdatedData data;
}
