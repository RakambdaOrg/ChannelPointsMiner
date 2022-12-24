package fr.rakambda.channelpointsminer.miner.api.passport.oauth.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse{
	@JsonProperty(value = "access_token")
	@Nullable
	private String accessToken;
	@JsonProperty(value = "refresh_token")
	@Nullable
	private String refreshToken;
	@Builder.Default
	@JsonProperty(value = "scope")
	@Nullable
	private Set<String> scope = new HashSet<>();
	@JsonProperty("token_type")
	@Nullable
	private String tokenType;
	@JsonProperty("status")
	@Nullable
	private String status;
	@JsonProperty("message")
	@Nullable
	private String message;
	
	@JsonIgnore
	public boolean isAuthorizationPending(){
		return Optional.ofNullable(message).map(m -> Objects.equals(m, "authorization_pending")).orElse(false);
	}
}
