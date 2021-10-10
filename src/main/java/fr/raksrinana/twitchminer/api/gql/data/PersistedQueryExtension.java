package fr.raksrinana.twitchminer.api.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class PersistedQueryExtension{
	@JsonProperty("version")
	private int version;
	@JsonProperty("sha256Hash")
	@NotNull
	private String sha256Hash;
}
