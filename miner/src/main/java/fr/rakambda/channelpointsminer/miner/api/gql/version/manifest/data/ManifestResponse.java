package fr.rakambda.channelpointsminer.miner.api.gql.version.manifest.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class ManifestResponse{
	@JsonProperty("channels")
	@NonNull
	@Builder.Default
	private List<ManifestChannel> channels = new ArrayList<>();
}
