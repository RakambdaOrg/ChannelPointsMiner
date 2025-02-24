package fr.rakambda.channelpointsminer.miner.api.hermes.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.message.eventupdated.EventUpdatedData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("event-updated")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class EventUpdated extends IHermesMessage {
	@JsonProperty("data")
	private EventUpdatedData data;
}
