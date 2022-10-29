package fr.rakambda.channelpointsminer.miner.api.ws.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public abstract class ITwitchWebSocketRequest{
	@JsonProperty("type")
	private String type;
	
	public ITwitchWebSocketRequest(String type){
		this.type = type;
	}
}
