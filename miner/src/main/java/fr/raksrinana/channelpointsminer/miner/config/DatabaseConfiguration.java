package fr.raksrinana.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
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
@JsonClassDescription("Database configuration.")
public class DatabaseConfiguration{
	@JsonProperty(value = "jdbcUrl", required = true)
	@JsonPropertyDescription(value = "JDBC connection URL.")
	@NotNull
	private String jdbcUrl;
	@JsonProperty("username")
	@JsonPropertyDescription(value = "Database username.")
	@Nullable
	private String username;
	@JsonProperty("password")
	@JsonPropertyDescription(value = "Database password.")
	@Nullable
	@ToString.Exclude
	private String password;
	@JsonProperty("maxPoolSize")
	@JsonPropertyDescription(value = "Maximum number of connections to the database. Default: 10")
	@Builder.Default
	private int maxPoolSize = 10;
}
