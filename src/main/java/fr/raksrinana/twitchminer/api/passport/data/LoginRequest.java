package fr.raksrinana.twitchminer.api.passport.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@Builder
public class LoginRequest{
	@Builder.Default
	@JsonProperty("undelete_user")
	private boolean undeleteUser = false;
	@Builder.Default
	@JsonProperty("remember_me")
	private boolean rememberMe = true;
	@NotNull
	@JsonProperty("username")
	private String username;
	@NotNull
	@JsonProperty("password")
	private String password;
	@Nullable
	@JsonProperty("authy_token")
	private String authyToken;
	@Nullable
	@JsonProperty("twitchguard_code")
	private String twitchGuardCode;
}
