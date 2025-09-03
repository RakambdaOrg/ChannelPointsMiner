package fr.rakambda.channelpointsminer.miner.api.passport.http.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Data
@Builder
public class LoginRequest{
	@JsonProperty(value = "client_id", required = true)
	@NonNull
	private String clientId;
	@Builder.Default
	@JsonProperty("undelete_user")
	private boolean undeleteUser = false;
	@Builder.Default
	@JsonProperty("remember_me")
	private boolean rememberMe = true;
	@NonNull
	@JsonProperty(value = "username", required = true)
	private String username;
	@NonNull
	@JsonProperty(value = "password", required = true)
	private String password;
	@Nullable
	@JsonProperty("authy_token")
	private String authyToken;
	@Nullable
	@JsonProperty("twitchguard_code")
	private String twitchGuardCode;
}
