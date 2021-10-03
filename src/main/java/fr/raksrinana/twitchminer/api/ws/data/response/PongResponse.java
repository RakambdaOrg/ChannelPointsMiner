package fr.raksrinana.twitchminer.api.ws.data.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("PONG")
@Getter
@ToString(callSuper = true)
public class PongResponse extends TwitchWebSocketResponse{
	public PongResponse(){
		super("PONG");
	}
}
