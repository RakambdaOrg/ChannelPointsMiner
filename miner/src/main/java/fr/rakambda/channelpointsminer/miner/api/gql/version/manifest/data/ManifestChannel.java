package fr.rakambda.channelpointsminer.miner.api.gql.version.manifest.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
public class ManifestChannel{
	@JsonProperty("id")
	@NonNull
	private String id;
	@JsonProperty("created")
	@JsonDeserialize(using = MillisecondsTimestampDeserializer.class)
	private Instant created;
	@JsonProperty("updated")
	@JsonDeserialize(using = MillisecondsTimestampDeserializer.class)
	private Instant updated;
	@JsonProperty("primary")
	private boolean primary;
	@JsonProperty("active")
	private boolean active;
	@JsonProperty("releases")
	@NonNull
	@Builder.Default
	private List<ManifestRelease> releases = new ArrayList<>();
}
