package fr.raksrinana.twitchminer.cli;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("FieldMayBeFinal")
@NoArgsConstructor
@Getter
@CommandLine.Command(name = "twitch-miner", mixinStandardHelpOptions = true)
public class CLIParameters{
	@Option(names = {
			"-s",
			"--settings"
	},
			description = "The configuration file")
	@NotNull
	private Path configurationFile = Paths.get("config.json");
}
