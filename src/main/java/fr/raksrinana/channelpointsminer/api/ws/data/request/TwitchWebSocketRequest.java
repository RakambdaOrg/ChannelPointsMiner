package fr.raksrinana.channelpointsminer.api.ws.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public abstract class TwitchWebSocketRequest{
	@JsonProperty("type")
	private String type;
	
	public TwitchWebSocketRequest(String type){
		this.type = type;
	}
}
