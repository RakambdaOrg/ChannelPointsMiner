package fr.raksrinana.channelpointsminer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Log4j2
public class StreamerDirectory{
	@JsonProperty("path")
	@NotNull
	@Comment(value = "Path to a folder that contains streamer configurations")
	private Path path;
	@JsonProperty("recursive")
	@Builder.Default
	@Comment(value = "If set to true, this folder will be scanned recursively", defaultValue = "false")
	private boolean recursive = false;
}
