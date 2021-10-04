package fr.raksrinana.twitchminer.api.ws.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("MESSAGE")
@Getter
@ToString(callSuper = true)
public class MessageResponse extends TwitchWebSocketResponse{
	@JsonProperty("data")
	private MessageData data;
	
	public MessageResponse(){
		super("RESPONSE");
	}
}
