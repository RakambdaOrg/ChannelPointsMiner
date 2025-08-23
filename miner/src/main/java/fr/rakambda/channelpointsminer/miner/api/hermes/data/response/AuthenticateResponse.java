package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.util.Objects;

@JsonTypeName("authenticateResponse")
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class AuthenticateResponse extends ITwitchHermesWebSocketResponse{
	@JsonProperty("authenticateResponse")
	private AuthenticateResponseData authenticateResponse;
	
	public boolean hasError(){
		return Objects.isNull(authenticateResponse) || !Objects.equals(authenticateResponse.result, "ok");
	}
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	@Builder
	public static class AuthenticateResponseData{
		@JsonProperty("result")
		private String result;
		@JsonProperty("error")
		private String error;
		@JsonProperty("errorCode")
		private String errorCode;
	}
}
