package fr.rakambda.channelpointsminer.miner.api.pubsub.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public abstract class ITwitchWebSocketRequest{
	@JsonProperty("type")
	private String type;
	
	public ITwitchWebSocketRequest(String type){
		this.type = type;
	}
}
