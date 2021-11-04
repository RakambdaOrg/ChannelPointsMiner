package fr.raksrinana.channelpointsminer.api.passport.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse{
	@Nullable
	@JsonProperty("access_token")
	private String accessToken;
	@Nullable
	@JsonProperty("redirect_path")
	private String redirectPath;
	@Nullable
	@JsonProperty("error")
	private String error;
	@Nullable
	@JsonProperty("error_code")
	private Integer errorCode;
	@Nullable
	@JsonProperty("error_description")
	private String errorDescription;
}
