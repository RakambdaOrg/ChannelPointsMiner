package fr.raksrinana.twitchminer.api.ws.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("RESPONSE")
@Getter
@ToString(callSuper = true)
public class ResponseResponse extends TwitchWebSocketResponse{
	@JsonProperty("error")
	private String error;
	@JsonProperty("nonce")
	private String nonce;
	
	public ResponseResponse(){
		super("RESPONSE");
	}
}
