package fr.raksrinana.channelpointsminer.api.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
