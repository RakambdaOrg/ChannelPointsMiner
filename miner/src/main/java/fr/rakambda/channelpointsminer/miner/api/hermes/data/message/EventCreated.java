package fr.rakambda.channelpointsminer.miner.api.hermes.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.message.eventcreated.EventCreatedData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("event-created")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class EventCreated extends IHermesMessage {
	@JsonProperty("data")
	private EventCreatedData data;
}
