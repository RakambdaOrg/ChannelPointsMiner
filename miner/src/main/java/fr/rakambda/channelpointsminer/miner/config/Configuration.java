package fr.rakambda.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonClassDescription("Global configuration.")
public class Configuration{
	@NonNull
	@JsonProperty(value = "accounts", required = true)
	@JsonPropertyDescription("List of account configurations.")
	private List<AccountConfiguration> accounts;
}
