package fr.rakambda.channelpointsminer.miner.cli;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.nio.file.Path;
import java.nio.file.Paths;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Command(name = "twitch-miner", mixinStandardHelpOptions = true)
public class CLIParameters{
	@Option(names = {
			"-s",
			"--settings"
	},
			description = "The configuration file")
	@NotNull
	@Builder.Default
	private Path configurationFile = Paths.get("config.json");
}
