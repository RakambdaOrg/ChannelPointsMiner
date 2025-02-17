package fr.rakambda.channelpointsminer.miner.api.pubsub.data.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = PongResponse.class, name = "PONG"),
		@JsonSubTypes.Type(value = ResponseResponse.class, name = "RESPONSE"),
		@JsonSubTypes.Type(value = MessageResponse.class, name = "MESSAGE"),
		@JsonSubTypes.Type(value = ReconnectResponse.class, name = "RECONNECT"),
})
@ToString
@EqualsAndHashCode
public abstract class ITwitchWebSocketResponse{
}
