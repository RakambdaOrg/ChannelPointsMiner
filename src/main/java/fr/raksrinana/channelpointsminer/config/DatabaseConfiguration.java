package fr.raksrinana.channelpointsminer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabaseConfiguration{
	@NotNull
	@JsonProperty("host")
	private String host;
	@JsonProperty("port")
	private int port;
	@NotNull
	@JsonProperty("database")
	private String database;
	@NotNull
	@JsonProperty("username")
	private String username;
	@NotNull
	@JsonProperty("password")
	private String password;
}
