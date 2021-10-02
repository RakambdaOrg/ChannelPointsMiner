package fr.raksrinana.twitchminer.api.passport.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
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
