package fr.rakambda.channelpointsminer.miner.api.passport.oauth.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponse{
	@JsonProperty(value = "device_code")
	@Nullable
	private String deviceCode;
	@Builder.Default
	@JsonProperty(value = "expires_in")
	private long expiresIn = 1800;
	@Builder.Default
	@JsonProperty("interval")
	private int interval = 5;
	@Nullable
	@JsonProperty(value = "user_code")
	private String userCode;
	@Nullable
	@JsonProperty(value = "verification_uri")
	private String verificationUri;
	@Nullable
	@JsonProperty(value = "status")
	private String status;
	@Nullable
	@JsonProperty(value = "message")
	private String message;
}
