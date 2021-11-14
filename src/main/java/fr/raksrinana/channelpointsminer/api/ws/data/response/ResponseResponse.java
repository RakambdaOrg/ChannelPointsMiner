package fr.raksrinana.channelpointsminer.api.ws.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import java.util.Objects;

@JsonTypeName("RESPONSE")
@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseResponse extends ITwitchWebSocketResponse{
	@JsonProperty("error")
	private String error;
	@JsonProperty("nonce")
	private String nonce;
	
	public boolean hasError(){
		return Objects.nonNull(getError()) && !getError().isBlank();
	}
}
