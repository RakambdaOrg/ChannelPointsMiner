package fr.raksrinana.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DatabaseConfiguration{
	@JsonProperty("jdbcUrl")
	@Comment(value = "JDBC connection URL")
	@NotNull
	private String jdbcUrl;
	@JsonProperty("username")
	@Comment(value = "Database username")
	@Nullable
	private String username;
	@JsonProperty("password")
	@Comment(value = "Database password")
	@Nullable
	@ToString.Exclude
	private String password;
}
