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
import java.nio.file.Path;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonClassDescription("Folder used to override streamer configurations.")
public class StreamerDirectory{
	@NotNull
	@JsonProperty(value = "path", required = true)
	@JsonPropertyDescription("Path to a folder that contains streamer configurations.")
	private Path path;
	@JsonProperty("recursive")
	@JsonPropertyDescription("If set to true, this folder will be scanned recursively. Default: false")
	@Builder.Default
	private boolean recursive = false;
}
