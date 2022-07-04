package fr.raksrinana.channelpointsminer.miner.api.passport.data;

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
	@JsonProperty("captcha_proof")
	@Nullable
	private String captchaProof;
	@JsonProperty("sms_proof")
	@Nullable
	private String smsProof;
	@JsonProperty("obscured_email")
	@Nullable
	private String obscuredEmail;
}
