package fr.raksrinana.channelpointsminer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Log4j2
public class Configuration{
	@NotNull
	@JsonProperty("accounts")
	@Comment(value = "List of account configurations.")
	private List<AccountConfiguration> accounts;
}
