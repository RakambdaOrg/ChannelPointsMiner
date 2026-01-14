package fr.rakambda.channelpointsminer.miner.api.gql.version.manifest.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.MillisecondsTimestampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class ManifestRelease{
	@JsonProperty("buildId")
	@NonNull
	private String buildId;
	@JsonProperty("created")
	@JsonDeserialize(using = MillisecondsTimestampDeserializer.class)
	private Instant created;
	@JsonProperty("stage")
	private String stage;
	@JsonProperty("files")
	@NonNull
	@Builder.Default
	private List<String> files = new ArrayList<>();
}
