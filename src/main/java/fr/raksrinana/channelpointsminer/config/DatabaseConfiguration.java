package fr.raksrinana.channelpointsminer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabaseConfiguration{
	@NotNull
	@JsonProperty("jdbcUrl")
	private String jdbcUrl;
	@Nullable
	@JsonProperty("username")
	private String username;
	@Nullable
	@JsonProperty("password")
	private String password;
}
