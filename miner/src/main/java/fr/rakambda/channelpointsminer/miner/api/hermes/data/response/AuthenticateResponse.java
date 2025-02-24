package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeName("authenticateResponse")
@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthenticateResponse extends ITwitchHermesWebSocketResponse {
	@JsonProperty("authenticateResponse")
	private String error;
	
	public boolean hasError(){
		return Objects.nonNull(getError()) && !getError().isBlank();
	}
}
