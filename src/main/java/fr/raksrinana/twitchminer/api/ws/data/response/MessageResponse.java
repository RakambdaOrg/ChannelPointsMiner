package fr.raksrinana.twitchminer.api.ws.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("MESSAGE")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class MessageResponse extends TwitchWebSocketResponse{
	@JsonProperty("data")
	private MessageData data;
}
