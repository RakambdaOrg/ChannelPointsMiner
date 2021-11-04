package fr.raksrinana.channelpointsminer.api.ws.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@JsonTypeName("MESSAGE")
@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageResponse extends TwitchWebSocketResponse{
	@JsonProperty("data")
	private MessageData data;
}
