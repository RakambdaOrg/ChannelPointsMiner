package fr.raksrinana.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Configuration{
	@NotNull
	@JsonProperty("accounts")
	@Comment(value = "List of account configurations.")
	private List<AccountConfiguration> accounts;
}
