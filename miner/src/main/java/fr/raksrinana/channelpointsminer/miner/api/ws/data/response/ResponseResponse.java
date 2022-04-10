package fr.raksrinana.channelpointsminer.miner.api.ws.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
