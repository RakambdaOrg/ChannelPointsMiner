package fr.raksrinana.twitchminer.api.ws.data.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
})
@ToString
public abstract class TwitchWebSocketResponse{
}
