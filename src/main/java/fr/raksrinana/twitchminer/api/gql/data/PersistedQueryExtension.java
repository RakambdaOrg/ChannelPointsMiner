package fr.raksrinana.twitchminer.api.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersistedQueryExtension{
	@JsonProperty("version")
	private int version;
	@JsonProperty("sha256Hash")
	@NotNull
	private String sha256Hash;
}
