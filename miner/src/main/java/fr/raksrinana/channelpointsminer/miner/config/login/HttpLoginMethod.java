package fr.raksrinana.channelpointsminer.miner.config.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.config.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;

@JsonTypeName("http")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpLoginMethod implements ILoginMethod{
	@NotNull
	@JsonProperty("password")
	@Comment(value = "Password of your Twitch account.")
	@ToString.Exclude
	private String password;
	@JsonProperty("use2FA")
	@Comment(value = "If this account uses 2FA set this to true to directly ask for it.", defaultValue = "false")
	@Builder.Default
	private boolean use2Fa = false;
	@NotNull
	@JsonProperty("authenticationFolder")
	@Comment(value = "Path to a folder that contains authentication used to log back in after a restart.", defaultValue = "./authentication")
	@Builder.Default
	private Path authenticationFolder = Paths.get("authentication");
}
