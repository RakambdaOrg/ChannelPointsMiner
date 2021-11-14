package fr.raksrinana.channelpointsminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.api.ws.data.message.eventcreated.EventCreatedData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("event-created")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class EventCreated extends IMessage{
	@JsonProperty("data")
	private EventCreatedData data;
}
