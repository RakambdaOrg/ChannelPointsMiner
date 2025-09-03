package fr.rakambda.channelpointsminer.miner.api.gql.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class PersistedQueryExtension{
	@JsonProperty("version")
	private int version;
	@JsonProperty("sha256Hash")
	@NonNull
	private String sha256Hash;
}
