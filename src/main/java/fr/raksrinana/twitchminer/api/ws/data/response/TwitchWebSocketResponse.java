package fr.raksrinana.twitchminer.api.ws.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
})
@ToString
public abstract class TwitchWebSocketResponse{
	@JsonProperty("type")
	private String type;
	
	public TwitchWebSocketResponse(String type){
		this.type = type;
	}
}
