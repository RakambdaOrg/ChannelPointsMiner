package fr.rakambda.channelpointsminer.miner.api.hermes.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString(callSuper = true)
@Getter
public class AuthenticateRequest extends ITwitchHermesWebSocketRequest{
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	private class Data{
		@JsonProperty("token")
		private String token;
	}
	
	@JsonProperty("authenticate")
	private Data authenticate;
	
	public AuthenticateRequest(@NotNull String token){
		super("authenticate");
		authenticate = new Data(token);
	}
}
